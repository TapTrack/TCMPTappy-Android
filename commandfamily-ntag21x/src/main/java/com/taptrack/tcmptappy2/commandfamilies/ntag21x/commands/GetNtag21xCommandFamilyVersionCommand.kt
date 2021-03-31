package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage

class GetNtag21xCommandFamilyVersionCommand : AbstractNtag21xMessage() {

    companion object {
        const val COMMAND_CODE: Byte = 0xFF.toByte()
    }

    override fun parsePayload(payload: ByteArray) = Unit

    override fun getPayload(): ByteArray = byteArrayOf()

    override fun getCommandCode(): Byte = COMMAND_CODE

}
