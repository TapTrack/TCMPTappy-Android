package com.taptrack.experiments.rancheria.ui.views.findtappies

import android.hardware.usb.UsbDevice
import com.taptrack.tcmptappy2.ble.TappyBleDeviceDefinition
import io.reactivex.Observable

data class ChooseTappiesViewState(
        val foundUsbDevices: Collection<UsbDevice>,
        val foundBleDevices: Collection<TappyBleDeviceDefinition>,
        val activeDevices: Collection<NamedTappy>,
        val bluetoothOn: Boolean
        ) {
    companion object {
        fun initialState(): ChooseTappiesViewState = ChooseTappiesViewState(emptySet(), emptySet(),emptySet(), false)
    }
}

interface ChooseTappiesViewModel {
    fun getFindTappiesState(): Observable<ChooseTappiesViewState>
    fun addActiveTappyBle(tappyBleDeviceDefinition: TappyBleDeviceDefinition)
    fun addActiveTappyUsb(usbTappy: UsbDevice)
    fun setBluetoothStataus(bluetoothOn: Boolean)
    fun removeActiveTappy(tappy: NamedTappy)
}


interface ChooseTappiesViewModelProvider {
    fun provideChooseTappiesViewModel(): ChooseTappiesViewModel
}

