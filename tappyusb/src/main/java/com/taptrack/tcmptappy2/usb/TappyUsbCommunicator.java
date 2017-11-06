package com.taptrack.tcmptappy2.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.taptrack.tcmptappy2.Tappy;
import com.taptrack.tcmptappy2.TappySerialCommunicator;

import java.util.concurrent.atomic.AtomicBoolean;

class TappyUsbCommunicator implements TappySerialCommunicator {
    private final Context context;
    private final UsbDevice device;
    private final UsbDeviceConnection connection;
    private final UsbSerialDevice serialDevice;

    private AtomicBoolean receiverRegistered = new AtomicBoolean(false);
    BroadcastReceiver disconnectionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null && device.equals(TappyUsbCommunicator.this.device)) {
                    TappyUsbCommunicator.this.close();
                }
            }
        }
    };

    private final Object opsLock = new Object();

    @Nullable
    private DataReceivedListener dataReceivedListener;
    @Nullable
    private Tappy.StatusListener statusListener;

    private final UsbSerialInterface.UsbReadCallback readCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            if(dataReceivedListener != null) {
                dataReceivedListener.recievedBytes(bytes);
            }
        }
    };

    private int currentStatus = Tappy.STATUS_DISCONNECTED;

    TappyUsbCommunicator(@NonNull Context context,
                         @NonNull UsbDevice device,
                         @NonNull UsbDeviceConnection connection) {
        this.context = context;
        this.device = device;
        this.connection = connection;
        serialDevice = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if(serialDevice == null) {
            throw new IllegalArgumentException("USB device could not be used as a USB serial device");
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
                    serialDevice.write(data);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean connect() {
        synchronized (opsLock) {
            if(currentStatus == Tappy.STATUS_DISCONNECTED) {
                notifyNewStatus(Tappy.STATUS_CONNECTING);
                if(serialDevice.open()) {
                    serialDevice.setBaudRate(115200);
                    serialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialDevice.setParity(UsbSerialInterface.PARITY_NONE);
                    serialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialDevice.read(readCallback);
                    notifyNewStatus(Tappy.STATUS_READY);
                    return true;
                } else {
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
            if(currentStatus == Tappy.STATUS_READY || currentStatus == Tappy.STATUS_CONNECTING) {
                notifyNewStatus(Tappy.STATUS_DISCONNECTING);
                serialDevice.close();
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
        return "TAPPYUSB-"+device.getDeviceName();
    }
}
