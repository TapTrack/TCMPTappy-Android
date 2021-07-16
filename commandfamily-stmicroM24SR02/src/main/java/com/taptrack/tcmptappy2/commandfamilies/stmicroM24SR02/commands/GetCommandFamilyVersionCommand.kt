package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class GetCommandFamilyVersionCommand : AbstractStMicroMessage() {

    companion object {
        const val COMMAND_CODE: Byte = 0xFE.toByte()
    }

    override fun parsePayload(payload: ByteArray) {
    }

    override fun getPayload(): ByteArray = byteArrayOf()

    override fun getCommandCode(): Byte = COMMAND_CODE
}
