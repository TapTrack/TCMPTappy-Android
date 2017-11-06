package com.taptrack.tcmptappy2.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.taptrack.tcmptappy2.SerialTappy;


public class TappyBle extends SerialTappy {

    /**
     * Instantiate a Tappy from a BluetoothDevice
     * using the standard service/characteristic UUIDs
     *
     * @param device device representing Tappy
     * @return a TappyBle backed by the
     */
    public static TappyBle getTappyBle(Context context, BluetoothDevice device) {
        TappyBleDeviceDefinition deviceDefinition = new ParcelableTappyBleDeviceDefinition(
                device.getName(),
                device.getAddress(),
                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_SERVICE_UUID,
                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_RX_CHARACTERISTIC_UUID,
                TappyBleDeviceDefinition.DEFAULT_TRUCONNECT_TX_CHARACTERISTIC_UUID);
        return getTappyBle(context, deviceDefinition);
    }

    /**
     * Instantiate a tappy from a TappyBleDeviceDefinition
     * @param context
     * @param deviceDefinition
     * @return
     */
    public static TappyBle getTappyBle(Context context, TappyBleDeviceDefinition deviceDefinition) {
        return new TappyBle(context, deviceDefinition);
    }

    private final TappyBleDeviceDefinition backingDefinition;

    TappyBle(Context context, TappyBleDeviceDefinition device) {
        super(new TappyBleCommunicator(context, device));
        backingDefinition = device;
    }

    public TappyBleDeviceDefinition getBackingDeviceDefinition() {
        return backingDefinition;
    }

    /**
     * For the current TappyBLE implementation, this
     * getDeviceDescription string can generally be assumed
     * to be universally unique. It is possible to have
     * two different TappyBLEs share a getDeviceDescription or the
     * same Tappy generate a different getDeviceDescription at different
     * times, but this would require non-standard firmware
     * or extensive hardware modification. As such, you can
     * safely assume that this getDeviceDescription is unique.
     *
     * @return String describing this TappyBle
     */
    @Override
    public String getDeviceDescription() {
        return super.getDeviceDescription();
    }
}
