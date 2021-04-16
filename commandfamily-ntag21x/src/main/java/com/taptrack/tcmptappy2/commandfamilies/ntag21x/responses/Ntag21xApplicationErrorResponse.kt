package com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage

class Ntag21xApplicationErrorResponse : AbstractNtag21xMessage {

    companion object {
        const val COMMAND_CODE: Byte = 0x7F
    }

    var appErrorCode: Byte = 0x00

    var internalErrorCode: Byte = 0x00

    var readerStatusCode: Byte = 0x00

    var errorDescription = ""

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size < 4) {
            throw MalformedPayloadException("Payload too short")
        }

        appErrorCode = payload[0]
        internalErrorCode = payload[1]
        readerStatusCode = payload[2]
        errorDescription = payload.decodeToString(startIndex = 3)
    }

    override fun getPayload(): ByteArray = byteArrayOf(
        appErrorCode,
        internalErrorCode,
        readerStatusCode,
        *errorDescription.toByteArray()
    )

    override fun getCommandCode(): Byte = COMMAND_CODE

}
