package com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage

class Ntag21xApplicationErrorResponse : AbstractNtag21xMessage {

    companion object {
        const val COMMAND_CODE: Byte = 0x7F
    }

    private var _appErrorCode: Byte = 0x00
    private var _internalErrorCode: Byte = 0x00
    private var _readerStatusCode: Byte = 0x00
    private var _errorDescription: String = ""

    var appErrorCode
        get() = _appErrorCode
        set(value) {
            _appErrorCode = value
        }

    var internalErrorCode
        get() = _internalErrorCode
        set(value) {
            _internalErrorCode = value
        }

    var readerStatusCode
        get() = _readerStatusCode
        set(value) {
            _readerStatusCode = value
        }

    var errorDescription
        get() = _errorDescription
        set(value) {
            _errorDescription = value
        }

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
