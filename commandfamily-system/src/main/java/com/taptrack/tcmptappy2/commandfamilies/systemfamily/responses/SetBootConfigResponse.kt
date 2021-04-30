package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses

import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage

class SetBootConfigResponse() : AbstractSystemMessage() {

    companion object {
        const val COMMAND_CODE: Byte = 0x18
    }

    override fun parsePayload(payload: ByteArray) = Unit

    override fun getPayload(): ByteArray = byteArrayOf()

    override fun getCommandCode(): Byte = COMMAND_CODE

}
