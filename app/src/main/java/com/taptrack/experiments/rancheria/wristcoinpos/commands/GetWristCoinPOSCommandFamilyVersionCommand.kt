package com.taptrack.experiments.rancheria.wristcoinpos.commands

import com.taptrack.experiments.rancheria.wristcoinpos.AbstractWristCoinPOSMessage

internal class GetWristCoinPOSCommandFamilyVersionCommand : AbstractWristCoinPOSMessage() {

    companion object {
        const val COMMAND_CODE: Byte = 0xFF.toByte()
    }

    override fun parsePayload(payload: ByteArray) = Unit

    override fun getPayload(): ByteArray = byteArrayOf()

    override fun getCommandCode(): Byte = COMMAND_CODE

}
