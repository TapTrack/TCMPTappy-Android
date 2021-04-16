package com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage

class GetNtag21xCommandFamilyVersionResponse : AbstractNtag21xMessage {

    companion object {
        const val COMMAND_CODE: Byte = 0x04
    }

    var majorVersion: Byte = 0x00
        private set

    var minorVersion: Byte = 0x00
        private set

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    override fun getPayload(): ByteArray = byteArrayOf(majorVersion, minorVersion)

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size < 2) {
            throw MalformedPayloadException("Payload too short")
        }

        majorVersion = payload[0]
        minorVersion = payload[1]
    }

    override fun getCommandCode(): Byte = COMMAND_CODE

}
