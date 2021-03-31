package com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage

class Ntag21xPollingTimeoutResponse : AbstractNtag21xMessage {

    companion object {
        const val COMMAND_CODE: Byte = 0x03
    }

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) = Unit

    override fun getPayload(): ByteArray = byteArrayOf()

    override fun getCommandCode(): Byte = COMMAND_CODE

}
