package com.taptrack.experiments.rancheria.business

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.os.*
import android.support.v7.app.NotificationCompat
import android.util.Log
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.getRancheriaApplication
import com.taptrack.experiments.rancheria.model.RealmTcmpCommunique
import com.taptrack.experiments.rancheria.ui.activities.MainActivity
import com.taptrack.tcmptappy.tcmp.MalformedPayloadException
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.BasicNfcCommandLibrary
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.NdefFoundResponse
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.PingCommand
import com.taptrack.tcmptappy.tcmp.common.CommandFamilyMessageResolver
import com.taptrack.tcmptappy.tcmp.common.FamilyCodeNotSupportedException
import com.taptrack.tcmptappy.tcmp.common.ResponseCodeNotSupportedException
import com.taptrack.tcmptappy2.RawTCMPMessage
import com.taptrack.tcmptappy2.TCMPMessage
import com.taptrack.tcmptappy2.TCMPMessageParseException
import com.taptrack.tcmptappy2.Tappy
import com.taptrack.tcmptappy2.ble.TappyBle
import com.taptrack.tcmptappy2.ble.TappyBleDeviceDefinition
import com.taptrack.tcmptappy2.tcmpconverter.TcmpConverter
import com.taptrack.tcmptappy2.usb.TappyUsb
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.realm.Realm
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock

interface TappyConnectionsListener {
    fun tappyConnectionsChanged(newCollection: Collection<Tappy>)
}

private sealed class TappyTrio {
    data class TappyBleTrio(
            val tappy: TappyBle,
            val statusListener: Tappy.StatusListener,
            val responseListener: Tappy.ResponseListener) : TappyTrio()
    data class TappyUsbTrio(val tappy: TappyUsb,
                            val statusListener: Tappy.StatusListener,
                            val responseListener: Tappy.ResponseListener) : TappyTrio()
}

class TappyService: Service() {

    private val connectionListenerSet = HashSet<TappyConnectionsListener>()

    private var allConnections = mutableMapOf<String,Tappy>()

    private var nonMutableConnections = emptySet<Tappy>()

    private val usbTappies = mutableMapOf<Int, TappyTrio.TappyUsbTrio>()
    private val bleTappies = mutableMapOf<String, TappyTrio.TappyBleTrio>()

    val connectionsRwLock = ReentrantReadWriteLock()

    private var realm: Realm? = null

    private var mtHandler = Handler(Looper.getMainLooper())

    private var autolaunchDisposable: Disposable? = null
    private var shouldAutolaunch: Boolean = false
    private var lastLaunched = 0.toLong()

    private var heartBeatDisposable: Disposable? = null

    private var wakeLock: PowerManager.WakeLock? = null

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(ACTION_DISCONNECT_ALL_TAPPIES == intent?.action) {
                connectionsRwLock.writeLock().lock()
                for (tappy in allConnections.values) {
                    tappy.close()
                }
                connectionsRwLock.writeLock().unlock()
            } else if (ACTION_SEND_MESSAGE == intent?.action) {
                if(intent.hasExtra(EXTRA_TCMP_MESSAGE)) {
                    val content = intent.getByteArrayExtra(EXTRA_TCMP_MESSAGE)
                    if(content != null) {
                        val msg = RawTCMPMessage(content)
                        sendTcmp(msg)
                    }
                }
            }
        }
    }

    inner class TappyServiceBinder : Binder() {
        fun registerConnectionsChangedListener(listener: TappyConnectionsListener,sendCurrent: Boolean) {
            connectionListenerSet.add(listener)
            if(sendCurrent) {
                listener.tappyConnectionsChanged(getCurrentImmutableConnections())
            }
        }

        fun unregisterConnectionsChangedListener(listener: TappyConnectionsListener) {
            connectionListenerSet.add(listener)
        }

        fun requestConnectToTappyBle(tappyBleDeviceDefinition: TappyBleDeviceDefinition) {
            connectTappyBle(tappyBleDeviceDefinition)
        }

        fun requestConnectToTappyUsb(usbDevice: UsbDevice) {
            connectTappyUsb(usbDevice)
        }
    }

    val binder = TappyServiceBinder()

    private fun getCurrentImmutableConnections(): Collection<Tappy> {
        return nonMutableConnections
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun sendTcmp(message: TCMPMessage) {
        try {
            connectionsRwLock.readLock().lock()
            allConnections.values
                    .filter { it.latestStatus == Tappy.STATUS_READY }
                    .forEach { it.sendMessage(message) }
            connectionsRwLock.readLock().unlock()

            mtHandler.post {
                realm?.executeTransactionAsync({
                    val recordObj = it.createObject(RealmTcmpCommunique::class.java, UUID.randomUUID().toString())
                    recordObj.deviceName = "ME"
                    recordObj.messageTime = System.currentTimeMillis()
                    recordObj.deviceId = "ME"
                    recordObj.isCommand = true
                    recordObj.message = message.toByteArray()
                })
            }
        } catch (ignored: TCMPMessageParseException) {
        }
    }

    override fun onCreate() {
        super.onCreate()
        wakeLock = (getSystemService(Context.POWER_SERVICE) as? PowerManager)
                ?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
        wakeLock?.setReferenceCounted(false)

        autolaunchDisposable = getRancheriaApplication().getAutolaunchEnabled()
                .subscribe {
                    shouldAutolaunch = it
                }


        heartBeatDisposable = Observables.combineLatest(
                getRancheriaApplication().getHeartbeatEnabled(),
                io.reactivex.Observable.interval(5,TimeUnit.SECONDS),
                fun(shouldHb : Boolean, _: Long) : Boolean = shouldHb
        ).toFlowable(BackpressureStrategy.LATEST)
        .subscribe {
            val hasReadyTappies: Boolean
            if (connectionsRwLock.readLock().tryLock()) {
                hasReadyTappies = allConnections.values
                        .filter { it.latestStatus == Tappy.STATUS_READY }
                        .isNotEmpty()
                connectionsRwLock.readLock().unlock()
            } else {
                hasReadyTappies = true
            }
            if (it && hasReadyTappies) {
                sendTcmp(TcmpConverter.toVersionTwo(PingCommand()))
            }
        }

        realm = Realm.getDefaultInstance()

        val filter = IntentFilter(ACTION_DISCONNECT_ALL_TAPPIES)
        filter.addAction(ACTION_SEND_MESSAGE)
        registerReceiver(broadcastReceiver,filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        autolaunchDisposable?.dispose()
        heartBeatDisposable?.dispose()
        realm?.close()
        unregisterReceiver(broadcastReceiver)
        wakeLock?.release()
    }

    private fun connectTappyBle(definition: TappyBleDeviceDefinition) {
        connectionsRwLock.writeLock().lock()

        if(bleTappies.containsKey(definition.address)){
            return
        }

        val tappy = TappyBle.getTappyBle(this,definition)
        val statusListener = object : Tappy.StatusListener {
            override fun statusReceived(status: Int) {
                when (status) {
                    Tappy.STATUS_READY,Tappy.STATUS_CONNECTING, Tappy.STATUS_DISCONNECTING -> {
                    }
                    Tappy.STATUS_DISCONNECTED, Tappy.STATUS_CLOSED, Tappy.STATUS_ERROR -> {
                        removeTappyBle(tappyBle = tappy)
                    }
                }
                notifyListenersOfChange(true)
            }
        }
        val responseListener = Tappy.ResponseListener {
            val response = it
            Log.i(TAG,"Received message from TappyBLE")
            mtHandler.post {
                realm?.executeTransactionAsync({
                    val recordObj = it.createObject(RealmTcmpCommunique::class.java,UUID.randomUUID().toString())
                    recordObj.deviceName = "TappyBLE: "+definition.name
                    recordObj.messageTime = System.currentTimeMillis()
                    recordObj.deviceId = tappy.deviceDescription
                    recordObj.isCommand = false
                    recordObj.message = response.toByteArray()
                })
                launchUrlIfNecessary(it)
            }
        }

        tappy.registerStatusListener(statusListener)
        tappy.registerResponseListener(responseListener)

        val trio = TappyTrio.TappyBleTrio(tappy,statusListener,responseListener)

        bleTappies.put(definition.address,trio)
        allConnections.put(tappy.deviceDescription,tappy)
        tappy.connect()

        connectionsRwLock.writeLock().unlock()

        notifyListenersOfChange()
        updateForegroundState()
    }

    private fun updateForegroundState() {
        var activeDeviceCount = 0

        connectionsRwLock.readLock().lock()

        if (allConnections.isNotEmpty()) {
            wakeLock?.acquire()

            val notificationTitle: String
            val notificationContent: String
            if (allConnections.size == 1) {
                notificationTitle = getString(R.string.active_tappies_notification_title)
                notificationContent = getString(R.string.one_tappy_active_notification_content)
            } else {
                notificationTitle = getString(R.string.active_tappies_notification_title)
                notificationContent = getString(R.string.multiple_tappies_active_notification_content, allConnections.size)
            }

            val disconnectTappiesIntent = Intent(ACTION_DISCONNECT_ALL_TAPPIES)
            val disconnectTappiesPendingIntent = PendingIntent.getBroadcast(this, 0, disconnectTappiesIntent, 0)

            val openActivityIntent = Intent(this, MainActivity::class.java)
            val openActivityPendingIntent = PendingIntent.getActivity(this,0,openActivityIntent,0)

            val notification = NotificationCompat.Builder(this)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationContent)
                    .setSmallIcon(R.drawable.ic_tappy_connected_notification)
                    .setTicker(notificationContent)
                    .setContentIntent(openActivityPendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .addAction(R.drawable.ic_remove_all, getString(R.string.remove_all_tappies), disconnectTappiesPendingIntent)
                    .build()
            startForeground(NOTIFICATION_ID, notification)

            // this makes the service actually started so it isn't killed
            val kickmyselfIntent = Intent(this, TappyService::class.java)
            startService(kickmyselfIntent)
        } else {
            stopForeground(true)
            stopSelf()
            wakeLock?.release()
        }

        connectionsRwLock.readLock().unlock()
    }

    fun launchUrlIfNecessary(message: TCMPMessage) {
        if(shouldAutolaunch) {
            try {
                val response = messageResolver.parseResponse(TcmpConverter.fromVersionTwo(message))
                if (response is NdefFoundResponse) {
                    ndefFound(response.message)
                }
            } catch (e: com.taptrack.tcmptappy.tcmp.TCMPMessageParseException) {
                Timber.e(e)
            } catch (e: FamilyCodeNotSupportedException) {
                Timber.e(e)
            } catch (e: ResponseCodeNotSupportedException) {
                Timber.e(e)
            } catch (e: MalformedPayloadException) {
                Timber.e(e)
            }
        }
    }

    fun ndefFound(message: NdefMessage) {
        val received = SystemClock.uptimeMillis()
        val records = message.records
        if (received - lastLaunched > THROTTLE_URL_MIN_TIME && records.isNotEmpty()) {
            val firstRecord = records[0]
            if (firstRecord.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(firstRecord.type, NdefRecord.RTD_URI)) {
                val uriPayload = firstRecord.payload
                if (uriPayload.size > 1) {
                    val prefixByte = uriPayload[0]
                    var url: String? = null
                    when (prefixByte) {
                        0x01.toByte() -> url = "http://www." + String(Arrays.copyOfRange(uriPayload, 1, uriPayload.size))
                        0x02.toByte() -> url = "https://www." + String(Arrays.copyOfRange(uriPayload, 1, uriPayload.size))
                        0x03.toByte() -> url = "http://" + String(Arrays.copyOfRange(uriPayload, 1, uriPayload.size))
                        0x04.toByte() -> url = "https://" + String(Arrays.copyOfRange(uriPayload, 1, uriPayload.size))
                    }

                    if (url != null) {
                        val launchUrlIntent = Intent(Intent.ACTION_VIEW)
                        launchUrlIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        launchUrlIntent.data = Uri.parse(url)
                        startActivity(launchUrlIntent)

                        lastLaunched = received
                    }
                }
            }
        }
    }
    private fun notifyListenersOfChange(onlyStatus: Boolean = false) {
        if(!onlyStatus) {
            connectionsRwLock.readLock().lock()

            nonMutableConnections = allConnections.values.toSet()

            connectionsRwLock.readLock().unlock()
        }

        val results = getCurrentImmutableConnections()

        for(listener in connectionListenerSet) {
            listener.tappyConnectionsChanged(results)
        }
    }

    private fun removeTappyBle(tappyBle:TappyBle) {
        connectionsRwLock.writeLock().lock()

        val trio = bleTappies[tappyBle.backingDeviceDefinition.address]
        if(trio != null) {
            val (tappy, statusListener, messageListener) = trio
            tappy.unregisterStatusListener(statusListener)
            tappy.unregisterResponseListener(messageListener)
            tappy.close()
            allConnections.remove(tappy.deviceDescription)
            bleTappies.remove(tappy.backingDeviceDefinition.address)
        }

        connectionsRwLock.writeLock().unlock()

        notifyListenersOfChange()
        updateForegroundState()
    }

    private fun connectTappyUsb(device: UsbDevice) {
        Log.i(TAG,"Attempting to connect to USB device")

        connectionsRwLock.writeLock().lock()

        if(usbTappies.containsKey(device.deviceId)){
            return
        }

        val tappy = TappyUsb.getTappyUsb(this,device)
        if(tappy == null) {
            Log.i(TAG,"Tappy was null")
            return
        }

        val statusListener = object : Tappy.StatusListener {
            override fun statusReceived(status: Int) {
                when (status) {
                    Tappy.STATUS_READY,Tappy.STATUS_CONNECTING, Tappy.STATUS_DISCONNECTING -> {
                    }
                    Tappy.STATUS_DISCONNECTED, Tappy.STATUS_CLOSED, Tappy.STATUS_ERROR -> {
                        removeTappyUsb(tappy)
                    }
                }
                notifyListenersOfChange()
            }
        }

        val responseListener = Tappy.ResponseListener {
            val response = it
            Log.i(TAG,"Received message from TappyUSB")
            mtHandler.post {
                realm?.executeTransactionAsync({
                    val recordObj = it.createObject(RealmTcmpCommunique::class.java,UUID.randomUUID().toString())
                    recordObj.deviceName = "TappyUSB"
                    recordObj.messageTime = System.currentTimeMillis()
                    recordObj.deviceId = tappy.deviceDescription
                    recordObj.isCommand = false
                    recordObj.message = response.toByteArray()
                })
                launchUrlIfNecessary(it)
            }
        }

        tappy.registerStatusListener(statusListener)
        tappy.registerResponseListener(responseListener)

        val trio = TappyTrio.TappyUsbTrio(tappy,statusListener,responseListener)

        usbTappies.put(device.deviceId,trio)
        allConnections.put(tappy.deviceDescription,tappy)
        notifyListenersOfChange()
        tappy.connect()

        connectionsRwLock.writeLock().unlock()
        updateForegroundState()
    }

    private fun removeTappyUsb(tappyUsb: TappyUsb) {
        connectionsRwLock.writeLock().lock()
        val trio = usbTappies[tappyUsb.backingUsbDevice.deviceId]
        if(trio != null) {
            val (tappy, statusListener, messageListener) = trio
            tappy.unregisterStatusListener(statusListener)
            tappy.unregisterResponseListener(messageListener)
            tappy.close()
            allConnections.remove(tappy.deviceDescription)
            usbTappies.remove(tappy.backingUsbDevice.deviceId)
        }

        connectionsRwLock.writeLock().unlock()

        notifyListenersOfChange()
        updateForegroundState()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    companion object {
        private val TAG = TappyService::class.java.name

        private val THROTTLE_URL_MIN_TIME: Long = 500

        private val NOTIFICATION_ID = 3415
        private val ACTION_DISCONNECT_ALL_TAPPIES = TappyService::class.java.name+".ACTION_DISCONNECT_ALL_TAPPIES"

        private val WAKELOCK_TAG = TappyService::class.java.name

        val ACTION_SEND_MESSAGE = TappyService::class.java.name+".ACTION_SEND_TCMP"
        private val EXTRA_TCMP_MESSAGE = TappyService::class.java.name+".EXTRA_TCMP_MESSAGE"

        private val messageResolver: CommandFamilyMessageResolver = CommandFamilyMessageResolver()

        init {
            messageResolver.registerCommandLibrary(BasicNfcCommandLibrary())
        }

        fun broadcastSendTcmp(message: TCMPMessage, ctx: Context) {
            val intent = Intent(ACTION_SEND_MESSAGE)
            intent.putExtra(EXTRA_TCMP_MESSAGE,message.toByteArray())
            ctx.sendBroadcast(intent)
        }
    }
}