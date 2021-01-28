package com.taptrack.tcmptappy2.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.taptrack.tcmptappy2.SerialTappy;
import com.taptrack.tcmptappy2.TappySerialCommunicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TappyUsb extends SerialTappy {
    private static final int VENDOR_ID_FTDI = 0x0403;
    private static final int PRODUCT_ID_FT232R = 0x6001;
    private static final int PRODUCT_ID_FT231X = 0x6015;

    private static final int VENDOR_ID_NATIVE_TAPPY = 0x04d8;
    private static final int PRODUCT_ID_NATIVE_TAPPY = 0x0053;

    private static final int VENDOR_ID_CDC_TAPPY = 0x03d8;
    private static final int PRODUCT_ID_CDC_TAPPY = 0x000a;

    private final UsbDevice backingDevice;

    TappyUsb(Context context, UsbDevice device, TappySerialCommunicator communicator) {
        super(communicator);
        this.backingDevice = device;
    }

    /**
     * Note that this will return all devices that *could* be a Tappy.
     * It will may also return other devices that use the same USB chip
     * @return List of devices or null if UsbManager system service is unavailable
     */
    @Nullable
    public static List<UsbDevice> getPotentialTappies(Context context) {
        UsbManager manager = (UsbManager) context.getApplicationContext().getSystemService(Context.USB_SERVICE);
        if(manager == null) {
            return null;
        }

        Map<String,UsbDevice> devices = manager.getDeviceList();
        List<UsbDevice> deviceList = new ArrayList<>();

        for(Map.Entry<String,UsbDevice> deviceEntry : devices.entrySet()) {
            UsbDevice device = deviceEntry.getValue();
            int vendorId = device.getVendorId();
            int productId = device.getProductId();
            if (vendorId == VENDOR_ID_FTDI) {
                if (productId == PRODUCT_ID_FT231X || productId == PRODUCT_ID_FT232R) {
                    deviceList.add(device);
                }
            } else if (vendorId == VENDOR_ID_CDC_TAPPY && productId == PRODUCT_ID_CDC_TAPPY) {
                deviceList.add(device);
            }else if (vendorId == VENDOR_ID_NATIVE_TAPPY && productId == PRODUCT_ID_NATIVE_TAPPY) {
                deviceList.add(device);
            }
        }

        return deviceList;
    }

    /**
     * Retrieve a Tappy instance for the TappyUSB.
     *
     * If the app does not have permission to communicate with the device or
     * if the UsbManager system service cannot be acquired, this can return null
     *
     * @param device the UsbDevice to communicate with
     * @return TappyUsb instance or null if
     */
    @Nullable
    public static TappyUsb getTappyUsb(@NonNull Context context, @NonNull UsbDevice device) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if(manager == null) {
            return null;
        }

        int vendorId = device.getVendorId();
        int productId = device.getProductId();
        if ((vendorId == VENDOR_ID_FTDI && (productId == PRODUCT_ID_FT231X || productId == PRODUCT_ID_FT232R)) ||
            vendorId == VENDOR_ID_CDC_TAPPY && productId == PRODUCT_ID_CDC_TAPPY) {
                UsbDeviceConnection connection = manager.openDevice(device);
                if(connection == null) {
                    return null;
                } else {
                    return new TappyUsb(
                            context,
                            device,
                            new TappyFTDIUsbCommunicator(context, device, connection)
                    );
                }
        } else if (vendorId == VENDOR_ID_NATIVE_TAPPY && device.getProductId() == PRODUCT_ID_NATIVE_TAPPY) {
            return new TappyUsb(
                    context,
                    device,
                    new TappyNativeUsbCommunicator(context, device, manager)
            );
        } else {
            return null;
        }
    }

    /**
     * Retrieve a getDeviceDescription for this TappyUSB
     * note that this getDeviceDescription is currently based on the
     * mount point Android assigns the Tappy, so the same Tappy
     * may report a different getDeviceDescription across reconnects and a
     * different Tappy connected later may have the same getDeviceDescription.
     * Therefore, once a TappyUSB enters a {@link com.taptrack.tcmptappy2.Tappy#STATUS_CLOSED} state
     * or hosting Context is killed, you should not count on this
     * getDeviceDescription to uniquely identify a single Tappy any more.
     *
     * @return String describing the Tappy
     */
    @Override
    public String getDeviceDescription() {
        return super.getDeviceDescription();
    }


    public UsbDevice getBackingUsbDevice() {
        return backingDevice;
    }
}
