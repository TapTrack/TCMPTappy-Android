package com.taptrack.tcmptappy2.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.annotation.NonNull;

public class UsbPermissionDelegate {
    private static final String ACTION_USB_PERMISSION = "com.taptrack.tappy.usb.USB_PERMISSION";

    public interface PermissionListener {
        void permissionDenied(UsbDevice device);
        void permissionGranted(UsbDevice device);
    }

    @NonNull
    private final Context context;
    @NonNull
    private final PendingIntent permissionIntent;
    @NonNull
    private final IntentFilter permissionFilter;
    @NonNull
    private final UsbManager usbManager;
    @NonNull
    private final PermissionListener listener;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION_USB_PERMISSION.equals(intent.getAction())) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)) {
                    if(device != null) {
                        listener.permissionGranted(device);
                    } else {
                        throw new IllegalStateException("Permission granted for no device");
                    }
                } else {
                    listener.permissionDenied(device);
                }

            }
        }
    };

    public UsbPermissionDelegate(@NonNull Context context,
                                 @NonNull PermissionListener listener) {
        this.listener = listener;
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if(usbManager == null) {
            throw new IllegalStateException("Must have a USB Manager");
        }
        this.context = context.getApplicationContext();
        permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION),0);
        permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
        this.usbManager = usbManager;
    }

    public UsbPermissionDelegate(@NonNull Context context,
                                 @NonNull UsbManager manager,
                                 @NonNull PermissionListener listener) {
        this.context = context.getApplicationContext();
        permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION),0);
        this.listener = listener;
        permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
        usbManager = manager;
    }

    public void register() {
        context.registerReceiver(receiver,permissionFilter);
    }

    public void requestPermission(UsbDevice device) {
        usbManager.requestPermission(device,permissionIntent);
    }

    public void unregister(){
        context.unregisterReceiver(receiver);
    }
}
