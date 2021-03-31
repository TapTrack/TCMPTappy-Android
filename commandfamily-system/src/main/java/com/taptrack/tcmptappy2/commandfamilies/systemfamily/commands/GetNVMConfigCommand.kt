package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands

import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage

class GetNVMConfigCommand : AbstractSystemMessage() {

    companion object {
        const val COMMAND_CODE: Byte = 0x17
    }

    override fun parsePayload(payload: ByteArray) = Unit

    override fun getPayload(): ByteArray = byteArrayOf()

    override fun getCommandCode(): Byte = COMMAND_CODE

}
