package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class PollingTimeoutResponse : AbstractStMicroMessage() {

    companion object {
        const val COMMAND_CODE: Byte = 0x01
    }

    override fun parsePayload(payload: ByteArray) {}

    override fun getPayload(): ByteArray = byteArrayOf()

    override fun getCommandCode(): Byte = COMMAND_CODE
}