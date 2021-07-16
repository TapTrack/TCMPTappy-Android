package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class LockNdefWriteAccessCommand : AbstractStMicroMessage {
    var timeout: Byte = 0
    var password: ByteArray = byteArrayOf()
    private var _rawTlvs: ByteArray = byteArrayOf()

    companion object {
        const val COMMAND_CODE: Byte = 0x03
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(IllegalArgumentException::class)
    constructor(timeout: Byte = 0, password: ByteArray = byteArrayOf()) : super() {
        this.timeout = timeout

        if(password.size == 16){
            this.password = password
        }else{
            throw IllegalArgumentException()
        }
        _rawTlvs = byteArrayOf(timeout) + password
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size != 17) {
            throw MalformedPayloadException("Payload length isnt 17")
        }
        timeout = payload[0]
        password = payload.sliceArray(1..16)
        _rawTlvs = payload
    }

    override fun getPayload(): ByteArray = _rawTlvs

    override fun getCommandCode(): Byte = COMMAND_CODE

}