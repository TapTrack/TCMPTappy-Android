package com.taptrack.experiments.rancheria.ui.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import com.jakewharton.rxrelay2.BehaviorRelay
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.*
import com.taptrack.experiments.rancheria.getRancheriaApplication
import com.taptrack.experiments.rancheria.model.RealmTcmpCommunique
import com.taptrack.experiments.rancheria.ui.getDrawableCompat
import com.taptrack.experiments.rancheria.ui.views.findtappies.*
import com.taptrack.experiments.rancheria.ui.views.sendmessages.CommandSelectorView
import com.taptrack.experiments.rancheria.ui.views.sendmessages.CommandSelectorViewModel
import com.taptrack.experiments.rancheria.ui.views.sendmessages.CommandSelectorViewModelImpl
import com.taptrack.experiments.rancheria.ui.views.sendmessages.CommandSelectorViewModelProvider
import com.taptrack.experiments.rancheria.ui.views.viewmessages.ViewMessagesView
import com.taptrack.tcmptappy2.Tappy
import com.taptrack.tcmptappy2.ble.TappyBle
import com.taptrack.tcmptappy2.ble.TappyBleDeviceDefinition
import com.taptrack.tcmptappy2.usb.TappyUsb
import com.taptrack.tcmptappy2.usb.UsbPermissionDelegate
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.realm.Realm
import org.jetbrains.anko.contentView
import org.jetbrains.anko.find
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.dip


class MainActivity : android.support.v7.app.AppCompatActivity(), ChooseTappiesViewModelProvider, CommandSelectorViewModelProvider {
    private val TAG = MainActivity::class.java.name

    private lateinit var permissionDelegate: UsbPermissionDelegate
    private lateinit var searchManager: SearchManagementDelegate
    private lateinit var commandSelectorViewModel: CommandSelectorViewModel
    private lateinit var commandDataSource: CommandDataSource

    private val handler = android.os.Handler(Looper.getMainLooper())

    private var realm: Realm? = null

    private var preferencesDisposable: Disposable? = null
    private var dayNightDisposable: Disposable? = null

    private var isAutolaunchingEnabled: Boolean = false
    private var isHeartbeatEnabled: Boolean = false
    private var isDayNightEnabled: Boolean = true

    private val recreateRunnable = Runnable {
        recreate()
    }

    private val sendMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(TappyService.ACTION_SEND_MESSAGE == intent?.action) {
                mainBottomNavigation?.selectedItemId = R.id.navigation_comm_history
            }
        }

    }

    private var serviceBinder: TappyService.TappyServiceBinder? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBinder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBinder = service as? TappyService.TappyServiceBinder
            registerWithService()
        }

    }

    private var usbPermissionListener: UsbPermissionDelegate.PermissionListener = object : UsbPermissionDelegate.PermissionListener {
        override fun permissionDenied(device: UsbDevice) {
            Log.i(TAG, "Permission denied")
        }

        override fun permissionGranted(device: UsbDevice) {
            Log.i(TAG, "Permission granted")
            connectToUsbDevice(device)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_manage_devices -> {
                viewpager?.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_send_message -> {
                viewpager?.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_comm_history -> {
                viewpager?.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val chooseTappiesViewModel = object: ChooseTappiesViewModel {
        override fun setBluetoothStataus(bluetoothOn: Boolean) {

            var newState: ChooseTappiesViewState? = null
            synchronized(stateMutationLock) {
                newState = state.copy(bluetoothOn = bluetoothOn)
                state = newState ?: state
            }

            if (newState != null) {
                stateRelay.accept(newState)
            }
        }

        private var state: ChooseTappiesViewState = ChooseTappiesViewState.initialState()
        private val stateMutationLock: Any = Object()
        private var stateRelay: BehaviorRelay<ChooseTappiesViewState> = BehaviorRelay.createDefault(state)

        fun setSearchResults(bleDevices: Collection<TappyBleDeviceDefinition>, usbDevices: Collection<UsbDevice>) {
            var newState: ChooseTappiesViewState? = null
            synchronized(stateMutationLock) {
                newState = state.copy(foundBleDevices = bleDevices, foundUsbDevices = usbDevices)
                state = newState ?: state
            }
            if(newState != null) {
                stateRelay.accept(newState as ChooseTappiesViewState)
            }
        }

        fun setActiveTappies(tappies: Collection<Tappy>) {
            val named = tappies.map {
                when(it) {
                    is TappyBle -> NamedTappy(tappy = it, name = it.backingDeviceDefinition.name)
                    is TappyUsb -> NamedTappy(tappy = it, name = getString(R.string.tappy_usb_name))
                    else -> NamedTappy(tappy = it, name = getString(R.string.unknown_tappy_name))
                }
            }
            var newState: ChooseTappiesViewState? = null
            synchronized(stateMutationLock) {
                newState = state.copy(activeDevices = named)
                state = newState ?: state
            }
            if(newState != null) {
                stateRelay.accept(newState as ChooseTappiesViewState)
            }
        }

        override fun addActiveTappyBle(tappyBleDeviceDefinition: TappyBleDeviceDefinition) {
            connectToBleDevice(device = tappyBleDeviceDefinition)
        }

        override fun addActiveTappyUsb(usbTappy: UsbDevice) {
            permissionDelegate.requestPermission(usbTappy)
        }

        override fun removeActiveTappy(tappy: NamedTappy) {
            removeTappy(tappy.tappy)
        }

        override fun getFindTappiesState(): Observable<ChooseTappiesViewState> {
            return stateRelay
        }

    }

    private val activeTappiesListener = object : TappyConnectionsListener {
        override fun tappyConnectionsChanged(newCollection: Collection<Tappy>) {
            chooseTappiesViewModel.setActiveTappies(newCollection)
        }

    }

    private val fineLocationListener = object: PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
            searchManager.fineLocationRequestResult(true)
        }

        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
            searchManager.fineLocationRequestResult(false)
        }
    }

    private val bluetoothReciever= object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                val state = intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF ->{
                        chooseTappiesViewModel.setBluetoothStataus(false)
                    }
                    BluetoothAdapter.STATE_TURNING_OFF ->{
                        chooseTappiesViewModel.setBluetoothStataus(false)
                    }
                    BluetoothAdapter.STATE_ON ->{
                        chooseTappiesViewModel.setBluetoothStataus(true)
                    }
                    BluetoothAdapter.STATE_TURNING_ON ->{
                        chooseTappiesViewModel.setBluetoothStataus(false)
                    }

                }
            }
        }
    }

    private var viewpager: ViewPager? = null
    private var mainBottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)

        commandDataSource = CommandDataSource(this)
        commandSelectorViewModel = CommandSelectorViewModelImpl(this,commandDataSource)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        permissionDelegate = UsbPermissionDelegate(this, usbPermissionListener)
        searchManager = SearchManagementDelegate(this,object : SearchResultsListener {
            override fun searchResultsUpdated(
                    bleDevices: Collection<TappyBleDeviceDefinition>, usbDevices: Collection<UsbDevice>) {
                chooseTappiesViewModel.setSearchResults(bleDevices,usbDevices)
            }
        })

        mainBottomNavigation = find<BottomNavigationView>(R.id.bnv_main_navigation) as BottomNavigationView
        mainBottomNavigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val snackbarPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                    .with(contentView as ViewGroup, R.string.fine_location_needed_rationale)
                    .withOpenSettingsButton(R.string.settings)
                    .build()
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(CompositePermissionListener(snackbarPermissionListener, fineLocationListener))
                    .check()
        } else {
            searchManager.fineLocationRequestResult(true)
        }


        viewpager = find<ViewPager>(R.id.vp_main_pager)
        val adapter = object: FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                if (position == 0) {
                    return ChooseDevicesFragment()
                } else if (position == 1) {
                    return SendMessagesFragment()
                } else if (position == 2) {
                    return CommHistoryFragment()
                } else {
                    throw IllegalArgumentException("Exceeded count")
                }
            }

            override fun getCount(): Int = 3
        }
        viewpager?.adapter = adapter
        viewpager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    searchManager.clearBluetoothDevices()
                    searchManager.requestActivate()
                } else {
                    searchManager.requestDeactivate()
                    searchManager.clearBluetoothDevices()
                }
                invalidateOptionsMenu()
            }
        })

        if(viewpager?.currentItem == 0) {
            searchManager.requestActivate()
        } else {
            searchManager.requestDeactivate()
        }

        addUsbDeviceFromIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        addUsbDeviceFromIntent(intent)
    }

    private fun addUsbDeviceFromIntent(intent: Intent?) {
        if (intent != null && intent.hasExtra(UsbManager.EXTRA_DEVICE)) {
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            connectToUsbDevice(device)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        if(viewpager?.currentItem == 2) {
            menuInflater.inflate(R.menu.app_options, menu)
        } else {
            menuInflater.inflate(R.menu.app_options_no_clear, menu)
        }

        val autolaunchItem = menu?.findItem(R.id.navigation_toggle_url_launch)
        if(isAutolaunchingEnabled) {
            autolaunchItem?.icon = getDrawableCompat(R.drawable.ic_link_white_24px)
            autolaunchItem?.title = getString(R.string.disable_url_launching)
        } else {
            autolaunchItem?.icon = getDrawableCompat(R.drawable.ic_link_off_white_24px)
            autolaunchItem?.title = getString(R.string.enable_url_launching)
        }

        val heartbeatItem = menu?.findItem(R.id.navigation_toggle_heartbeat)
        if(isHeartbeatEnabled) {
            heartbeatItem?.icon = getDrawableCompat(R.drawable.ic_heartbeat_border_white_24dp)
            heartbeatItem?.title = getString(R.string.disable_heartbeat)
        } else {
            heartbeatItem?.icon = getDrawableCompat(R.drawable.ic_heartbeat_off_white_24px)
            heartbeatItem?.title = getString(R.string.enable_heartbeat)
        }

        val daynightItem = menu?.findItem(R.id.navigation_toggle_daynight)
        if(isDayNightEnabled) {
            daynightItem?.icon = getDrawableCompat(R.drawable.ic_day_night_enabled_white_24px)
            daynightItem?.title = getString(R.string.disable_daynight_mode)
        } else {
            daynightItem?.icon = getDrawableCompat(R.drawable.ic_day_mode_white_24px)
            daynightItem?.title = getString(R.string.enable_daynight_mode)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.navigation_clear_history -> {
                realm?.executeTransactionAsync {
                    it.where(RealmTcmpCommunique::class.java).findAll().deleteAllFromRealm()
                }
                return true
            }
            R.id.navigation_toggle_url_launch -> {
                val shouldEnable = !isAutolaunchingEnabled
                getRancheriaApplication().setAutolaunchEnabled(shouldEnable)
                if(shouldEnable) {
                    Snackbar.make(viewpager!!,R.string.automatic_url_launching_enabled_snackbar_msg,Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(viewpager!!,R.string.automatic_url_launching_disabled_snackbar_msg,Snackbar.LENGTH_SHORT).show()
                }
                return true
            }
            R.id.navigation_toggle_daynight -> {
                val shouldEnable = !isDayNightEnabled
                getRancheriaApplication().setNightModeEnabled(shouldEnable)
                if(shouldEnable) {
                    Snackbar.make(viewpager!!,R.string.daynight_mode_enabled_snackbar_msg,Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(viewpager!!,R.string.daynight_mode_disabled_snackbar_msg,Snackbar.LENGTH_SHORT).show()
                }
                return true
            }
            R.id.navigation_toggle_heartbeat -> {
                val shouldEnable = !isHeartbeatEnabled
                getRancheriaApplication().setHeartbeatEnabled(shouldEnable)
                if(shouldEnable) {
                    Snackbar.make(viewpager!!,R.string.heartbeat_enabled_snackbar_msg,Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(viewpager!!,R.string.heartbeat_disabled_snackbar_msg,Snackbar.LENGTH_SHORT).show()
                }
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun provideChooseTappiesViewModel(): ChooseTappiesViewModel {
        return chooseTappiesViewModel
    }

    private fun connectToUsbDevice(device: UsbDevice) {
        handler.postDelayed({
            serviceBinder?.requestConnectToTappyUsb(device)
        }, 32)
    }

    private fun connectToBleDevice(device: TappyBleDeviceDefinition) {
        serviceBinder?.requestConnectToTappyBle(device)
    }

    private fun removeTappy(tappy: Tappy) {
        tappy.close()
    }

    private fun registerWithService() {
        serviceBinder?.registerConnectionsChangedListener(activeTappiesListener,true)
    }

    private fun unregisterFromService() {
        serviceBinder?.unregisterConnectionsChangedListener(activeTappiesListener)
    }

    private fun postRecreate(delay: Long) {
        cancelPendingRecreate()
        handler.postDelayed(recreateRunnable,delay)
    }

    private fun cancelPendingRecreate(){
        handler.removeCallbacks(recreateRunnable)
    }

    override fun onStart() {
        super.onStart()

        val app = getRancheriaApplication()
        preferencesDisposable = Observables.combineLatest(
                app.getAutolaunchEnabled(),
                app.getHeartbeatEnabled(),
                app.getNightModeEnabled(),
                ::Triple
        ).subscribe {
            isAutolaunchingEnabled = it.first
            isHeartbeatEnabled  = it.second
            isDayNightEnabled = it.third
            runOnUiThread {
                invalidateOptionsMenu()
            }
        }

        dayNightDisposable = getRancheriaApplication().getNightModeEnabled()
                .skip(1)
                .distinctUntilChanged()
                .subscribe {
                    postRecreate(2500)
                }

        realm = Realm.getDefaultInstance()
        permissionDelegate.register()
        bindService(Intent(this, TappyService::class.java),serviceConnection, Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT)

        val sendFilter = IntentFilter(TappyService.ACTION_SEND_MESSAGE)
        registerReceiver(sendMessageReceiver,sendFilter)
    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothReciever, filter)
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            chooseTappiesViewModel.setBluetoothStataus(bluetoothAdapter.isEnabled)
        } else {
            chooseTappiesViewModel.setBluetoothStataus(false)
        }
        searchManager.resume()
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(bluetoothReciever)
        searchManager.pause()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(sendMessageReceiver)

        dayNightDisposable?.dispose()
        preferencesDisposable?.dispose()
        cancelPendingRecreate()

        unregisterFromService()
        unbindService(serviceConnection)
        permissionDelegate.unregister()
        realm?.close()
        realm = null
    }

    override fun provideCommandSelectorViewModel(): CommandSelectorViewModel {
        return commandSelectorViewModel
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}

class SendMessagesFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ctx = container!!.context
        val v = CommandSelectorView(ctx)
        v.layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        v.setPadding(dip(4),0,dip(4),0)
        return v
    }
}

class ChooseDevicesFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = ChooseTappiesView(container!!.context)
        v.layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        return v
    }
}

class CommHistoryFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val ctx = container!!.context
        val v2 = FrameLayout(ctx)
        v2.layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        val v = ViewMessagesView(ctx)
        v.layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
        v.setPadding(dip(8),dip(8),dip(8),dip(8))
        v.clipToPadding = false
        v2.addView(v)
        return v2
    }
}