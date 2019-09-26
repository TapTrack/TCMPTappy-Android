package com.taptrack.experiments.rancheria.ui.views.sendmessages

import com.taptrack.tcmptappy2.AbstractTCMPMessage
import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetConfigItemCommand

class DisableBlePinCommand : AbstractTCMPMessage() {
    private val wrappedCommand = SetConfigItemCommand(
            SetConfigItemCommand.ParameterBytes.DISABLE_BLUETOOTH_PIN_PAIRING
    )

    override fun parsePayload(payload: ByteArray) {
        wrappedCommand.parsePayload(payload)
        if (wrappedCommand.parameter != SetConfigItemCommand.ParameterBytes.DISABLE_BLUETOOTH_PIN_PAIRING) {
            throw MalformedPayloadException("must be a disable bluetooth pin pairing command")
        }
    }

    override fun getPayload(): ByteArray {
        return wrappedCommand.payload
    }

    override fun getCommandCode(): Byte = wrappedCommand.commandCode

    override fun getCommandFamily(): ByteArray = wrappedCommand.commandFamily


}