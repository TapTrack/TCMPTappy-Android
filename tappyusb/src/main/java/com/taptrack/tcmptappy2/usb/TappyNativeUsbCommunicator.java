package com.taptrack.tcmptappy2.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taptrack.tcmptappy2.Tappy;
import com.taptrack.tcmptappy2.TappySerialCommunicator;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okio.ByteString;

class TappyNativeUsbCommunicator implements TappySerialCommunicator {
    private static final String TAG = TappyNativeUsbCommunicator.class.getName();
    private final Context context;
    private final UsbDevice usbDevice;
    private final UsbManager usbManager;

    private NativeReadThread readThread = null;
    private DrainAndCloseAsyncRequestsThread drainThread = null;

    @Nullable
    private UsbDeviceConnection usbConnection = null;
    @Nullable
    private UsbEndpoint usbInEndpoint = null;
    @Nullable
    private UsbEndpoint usbOutEndpoint = null;
    @Nullable
    private UsbInterface usbInterface = null;

//    private static final int SEND_TIMEOUT_MS = 100;

//    private volatile boolean closeHead = false;
//    private AsyncHead asyncHead = new AsyncHead();

    private AtomicBoolean receiverRegistered = new AtomicBoolean(false);
    BroadcastReceiver disconnectionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null && device.equals(TappyNativeUsbCommunicator.this.usbDevice)) {
                    TappyNativeUsbCommunicator.this.close();
                }
            }
        }
    };

    private final Object opsLock = new Object();

    @Nullable
    private DataReceivedListener dataReceivedListener;
    @Nullable
    private Tappy.StatusListener statusListener;

    private final NativeReadThread.ReadCallback readCallback = new NativeReadThread.ReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            Log.d(TAG,String.format("received length %d: %s", bytes.length, ByteString.of(bytes).hex()));
            if(dataReceivedListener != null) {
                dataReceivedListener.recievedBytes(bytes);
            }
        }
    };

    private int currentStatus = Tappy.STATUS_DISCONNECTED;

    TappyNativeUsbCommunicator(@NonNull Context context,
                               @NonNull UsbDevice device,
                               @NonNull UsbManager manager) {
        this.context = context;
        this.usbDevice = device;
        this.usbManager = manager;

        this.usbInterface = this.usbDevice.getInterface(0);
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                    this.usbInEndpoint = endpoint;
                } else if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT){
                    // this could just be else, but using else-if for clarity
                    this.usbOutEndpoint = endpoint;
                }
            }

        }



        if(receiverRegistered.compareAndSet(false,true)) {
            // this atomic operation isn't really necessary, but probably
            // best to do it anyway in case this code gets moved somewhere else
            context.registerReceiver(disconnectionReceiver,
                    new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
        }
    }


    private void notifyNewStatus(@Tappy.TappyStatus int status) {
        currentStatus = status;
        if(statusListener != null) {
            statusListener.statusReceived(status);
        }
    }

    @Override
    public void setDataListener(@NonNull DataReceivedListener listener) {
        dataReceivedListener = listener;
    }

    @Override
    public void removeDataListener() {
        dataReceivedListener = null;
    }

    @Override
    public void setStatusListener(@NonNull Tappy.StatusListener listener) {
        statusListener = listener;
    }

    @Override
    public void removeStatusListener() {
        statusListener = null;
    }

    @Override
    public int getStatus() {
        return currentStatus;
    }

    @Override
    public boolean sendBytes(@NonNull byte[] data) {
        if(currentStatus == Tappy.STATUS_READY) {
            synchronized (opsLock) {
                if(currentStatus == Tappy.STATUS_READY) {
                    UsbRequest req = new UsbRequest();
                    if (!req.initialize(usbConnection,usbOutEndpoint)) {
                        return false;
                    }
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    req.setClientData(buffer);
                    req.queue(buffer,data.length);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean connect() {
        if (usbInEndpoint == null || usbOutEndpoint == null) {
            Log.e(TAG,"missing communication endpoints");
            notifyNewStatus(Tappy.STATUS_ERROR);
            return false;
        }
        synchronized (opsLock) {
            if(currentStatus == Tappy.STATUS_DISCONNECTED) {
                notifyNewStatus(Tappy.STATUS_CONNECTING);


                UsbDeviceConnection usbConnection = this.usbManager.openDevice(this.usbDevice);
                if (usbConnection == null) {
                    Log.d(TAG,"unable to open device connection");
                    notifyNewStatus(Tappy.STATUS_ERROR);
                    return false;
                }

                if(usbConnection.claimInterface(usbInterface, true)) {
                    this.usbConnection = usbConnection;
                    readThread = new NativeReadThread(
                            usbConnection,
                            usbInEndpoint,
                            readCallback
                    );
                    drainThread = new DrainAndCloseAsyncRequestsThread(usbConnection);
                    readThread.start();
                    drainThread.start();

                    notifyNewStatus(Tappy.STATUS_READY);
                    return true;
                } else {
                    Log.d(TAG,"unable to claim interface");
                    usbConnection.close();
                    this.usbConnection = null;
                    notifyNewStatus(Tappy.STATUS_ERROR);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean disconnect() {
        synchronized (opsLock) {
            if(currentStatus == Tappy.STATUS_READY) {
                notifyNewStatus(Tappy.STATUS_DISCONNECTING);
                readThread.close();
                readThread = null;
                drainThread.close();
                drainThread = null;
                usbConnection.releaseInterface(usbInterface);
                this.usbConnection.close();
                this.usbConnection = null;
                notifyNewStatus(Tappy.STATUS_DISCONNECTED);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean close() {
        synchronized (opsLock) {
            boolean disconnectSucceeded = true;
            if(currentStatus != Tappy.STATUS_DISCONNECTED) {
                disconnectSucceeded = disconnect();
            }
            if(receiverRegistered.compareAndSet(true,false)) {
                context.unregisterReceiver(disconnectionReceiver);
            }
            notifyNewStatus(Tappy.STATUS_CLOSED);
            return disconnectSucceeded;
        }
    }

    @Override
    public String getDeviceDescription() {
        return "TAPPYUSB-"+ usbDevice.getDeviceName();
    }


    private static class DrainAndCloseAsyncRequestsThread extends Thread {
        private volatile boolean shouldRun = true;

        private final UsbDeviceConnection connection;

        private DrainAndCloseAsyncRequestsThread(UsbDeviceConnection connection) {
            this.connection = connection;
        }


        private void close() {
            shouldRun = false;
        }

        @Override
        public void run() {
            super.run();
            while (shouldRun) {
                // the documentation makes it unclear if this is necessary
                UsbRequest request = connection.requestWait();
                if (request != null) {
                    request.close();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}
            }
        }

    }

    private static class NativeReadThread extends Thread {
        private UsbEndpoint endpoint;
        private ReadCallback callback;
        private UsbDeviceConnection connection;
        private volatile boolean closed = false;

        private final int bufferSize;

        interface ReadCallback {
            public void onReceivedData(byte[] bytes);
        }

        public NativeReadThread(
                UsbDeviceConnection connection,
                UsbEndpoint endpoint,
                ReadCallback callback
        ) {
            super();
            this.connection = connection;
            this.endpoint = endpoint;
            this.callback = callback;

            bufferSize = Math.min(MAX_BUFFER_SIZE,endpoint.getMaxPacketSize());
        }


        public void close() {
            closed = true;
        }

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[bufferSize];
            while (!closed) {
                int readBytes = connection.bulkTransfer(
                        endpoint,
                        buffer,
                        buffer.length,
                        READ_TIMEOUT_MS
                );
                if (readBytes <= 0) {
                    // this exists to keep loop from using up a lot of CPU due
                    // to tight looping
                    try {
                        TimeUnit.MICROSECONDS.sleep(
                                ACTIVE_READ_SLEEP_TIME_MICROS
                        );
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    byte[] readData = new byte[readBytes];
                    System.arraycopy(buffer, 0, readData, 0, readBytes);
                    callback.onReceivedData(readData);
                }

            }
        }

        private static final int MAX_BUFFER_SIZE = 4096;
        private static final int READ_TIMEOUT_MS = 10;
        private static final int ACTIVE_READ_SLEEP_TIME_MICROS = 1;

    }
}
