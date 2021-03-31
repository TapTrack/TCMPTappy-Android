package com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses

import com.taptrack.tcmptappy2.MalformedPayloadException

class Ntag21xWriteSuccessResponse : AbstractSuccessResponse {

    companion object {
        const val COMMAND_CODE: Byte = 0x05
    }

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.isEmpty()) {
            throw MalformedPayloadException("Payload too short")
        }

        tagType = payload[0]

        if (payload.size > 1) {
            uid = payload.sliceArray(1..payload.lastIndex)
        }
    }

    override fun getPayload(): ByteArray = byteArrayOf(tagType, *uid)

    override fun getCommandCode(): Byte = COMMAND_CODE

}
