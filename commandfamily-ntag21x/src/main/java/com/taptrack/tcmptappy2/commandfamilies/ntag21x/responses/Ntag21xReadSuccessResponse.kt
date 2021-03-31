package com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses

import com.taptrack.tcmptappy2.MalformedPayloadException

class Ntag21xReadSuccessResponse : AbstractSuccessResponse {

    companion object {
        const val COMMAND_CODE: Byte = 0x01
    }

    private var _ndefMessage: ByteArray = byteArrayOf()

    var ndefMessage: ByteArray
        get() = _ndefMessage
        private set(value) {
            _ndefMessage = value
        }

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size < 3) {
            throw MalformedPayloadException("Payload too short")
        }

        tagType = payload[0]

        val uidLengthByte = payload[1]
        val uidLength = uidLengthByte.toInt()

        if (payload.size - 2 < uidLength) {
            throw MalformedPayloadException("Payload too short")
        }

        val uidLastIndex = (2 + uidLength) - 1
        uid = payload.sliceArray(2..uidLastIndex)

        if (payload.size > 2 + uidLength) {
            val messageStartIndex = uidLastIndex + 1
            ndefMessage = payload.sliceArray(messageStartIndex..payload.lastIndex)
        }
    }

    override fun getPayload(): ByteArray = byteArrayOf(
        tagType,
        *uidLengthBytes,
        *uid,
        *ndefMessage,
    )

    override fun getCommandCode(): Byte = COMMAND_CODE

}
