package com.taptrack.experiments.rancheria.ui.views.sendmessages

import com.taptrack.tcmptappy2.AbstractTCMPMessage
import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetConfigItemCommand

class SetBLEPinCommand : AbstractTCMPMessage {
    private val wrappedCommand = SetConfigItemCommand(
            SetConfigItemCommand.ParameterBytes.ENABLE_BLUETOOTH_PIN_PARING
    )
    private var pin: String

    constructor() : this("000")

    constructor(pin: String) {
        this.pin = pin
        wrappedCommand.multibyteValue = pin.toByteArray(Charsets.US_ASCII)
    }

    fun setPin(newPin: String) {
        this.pin = newPin
        wrappedCommand.multibyteValue = newPin.toByteArray(Charsets.US_ASCII)
    }

    fun getPin(): String {
        return pin
    }

    override fun parsePayload(payload: ByteArray) {
        wrappedCommand.parsePayload(payload)
        if (wrappedCommand.parameter != SetConfigItemCommand.ParameterBytes.ENABLE_BLUETOOTH_PIN_PARING) {
            throw MalformedPayloadException("must be a bluetooth pin pairing command")
        }
        pin = String(
                bytes = wrappedCommand.multibyteValue,
                charset = Charsets.US_ASCII
        )
    }

    override fun getPayload(): ByteArray {
        return wrappedCommand.payload
    }

    override fun getCommandCode(): Byte = wrappedCommand.commandCode

    override fun getCommandFamily(): ByteArray = wrappedCommand.commandFamily


}