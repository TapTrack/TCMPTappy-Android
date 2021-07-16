package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class Type4ErrorResponse : AbstractStMicroMessage {
    var sw1: Byte = 0x00
    var sw2: Byte = 0x00
    private var _rawTlvs: ByteArray = byteArrayOf()

    companion object {
        const val COMMAND_CODE: Byte = 0x02
    }

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if(payload.size != 2){
            throw MalformedPayloadException()
        }
        sw1 = payload[0]
        sw2 = payload[1]
        _rawTlvs = payload
    }

    override fun getPayload(): ByteArray = _rawTlvs

    override fun getCommandCode(): Byte = COMMAND_CODE

}