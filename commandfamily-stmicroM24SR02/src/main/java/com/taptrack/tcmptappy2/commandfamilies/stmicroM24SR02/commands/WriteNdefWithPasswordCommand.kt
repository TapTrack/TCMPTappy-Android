package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import com.taptrack.kotlin_tlv.ByteUtils.intToArray
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage


class WriteNdefWithPasswordCommand : AbstractStMicroMessage{

    var timeout: Byte = 0x00
    var password: ByteArray = byteArrayOf()
    var content: ByteArray = byteArrayOf()
    var length: ByteArray = byteArrayOf()
    private var _rawTlvs: ByteArray = byteArrayOf()

    companion object {
        const val COMMAND_CODE: Byte = 0x09
    }

    constructor() : super()

    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(IllegalArgumentException::class)
    constructor(timeout: Byte = 0, password: ByteArray = byteArrayOf(), ndefMessage: ByteArray = byteArrayOf()) : super() {
        this.timeout = timeout

        if(password.size == 16){
            this.password = password
        }else{
            throw IllegalArgumentException()
        }

        length = intToArray(ndefMessage.size).sliceArray(2..3)
        content = ndefMessage
        _rawTlvs = byteArrayOf(timeout) + password + length + content
    }


    override fun parsePayload(payload: ByteArray) {
        timeout = payload[0]
        password = payload.sliceArray(1..16)
        length = byteArrayOf(payload[17], payload[18])
        content = payload.sliceArray(19..(payload.size-1))
        _rawTlvs = payload
    }

    override fun getPayload(): ByteArray = _rawTlvs

    override fun getCommandCode(): Byte = COMMAND_CODE

}