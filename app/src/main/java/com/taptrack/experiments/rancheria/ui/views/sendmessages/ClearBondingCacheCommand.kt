package com.taptrack.experiments.rancheria.ui.views.sendmessages

import com.taptrack.tcmptappy2.AbstractTCMPMessage
import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetConfigItemCommand

class ClearBondingCacheCommand : AbstractTCMPMessage() {
    private val wrappedCommand = SetConfigItemCommand(
        SetConfigItemCommand.ParameterBytes.CLEAR_BLUETOOTH_BONDING_CACHE
    )

    override fun parsePayload(payload: ByteArray) {
        wrappedCommand.parsePayload(payload)
        if (wrappedCommand.parameter != SetConfigItemCommand.ParameterBytes.CLEAR_BLUETOOTH_BONDING_CACHE) {
            throw MalformedPayloadException("must be a clear blutooth bonding cache command")
        }
    }

    override fun getPayload(): ByteArray {
        return wrappedCommand.payload
    }

    override fun getCommandCode(): Byte = wrappedCommand.commandCode

    override fun getCommandFamily(): ByteArray = wrappedCommand.commandFamily

}