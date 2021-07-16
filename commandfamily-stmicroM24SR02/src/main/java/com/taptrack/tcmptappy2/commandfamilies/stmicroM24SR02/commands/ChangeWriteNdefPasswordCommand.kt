package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class ChangeWriteNdefPasswordCommand : AbstractStMicroMessage {
    var timeout: Byte = 0
    var currentPassword: ByteArray = byteArrayOf()
    var newPassword: ByteArray = byteArrayOf()
    private var _rawTlvs: ByteArray = byteArrayOf()


    companion object {
        const val COMMAND_CODE: Byte = 0x02
    }

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(IllegalArgumentException::class)
    constructor(timeout: Byte = 0, currentPassword: ByteArray = byteArrayOf(), newPassword: ByteArray = byteArrayOf()) : super() {
        this.timeout = timeout

        if(currentPassword.size == 16){
            this.currentPassword = currentPassword
        }else{
            throw IllegalArgumentException()
        }

        if(newPassword.size == 16){
            this.newPassword = newPassword
        }else{
            throw IllegalArgumentException()
        }
        _rawTlvs = byteArrayOf(timeout) + currentPassword + newPassword
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size != 33) {
            throw MalformedPayloadException("Payload length is less than 16")
        }
        timeout = payload[0]
        currentPassword = payload.sliceArray(1..16)
        newPassword = payload.sliceArray(17..32)
        _rawTlvs = payload
    }

    override fun getPayload(): ByteArray = _rawTlvs

    override fun getCommandCode(): Byte = COMMAND_CODE

}