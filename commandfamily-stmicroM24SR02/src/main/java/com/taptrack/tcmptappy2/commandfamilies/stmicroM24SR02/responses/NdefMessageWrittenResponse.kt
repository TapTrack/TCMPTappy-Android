package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class NdefMessageWrittenResponse : AbstractStMicroMessage {
    var uid: ByteArray = byteArrayOf()
    private var _rawTlvs: ByteArray = byteArrayOf()

    companion object {
        const val COMMAND_CODE: Byte = 0x0B
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    override fun parsePayload(payload: ByteArray) {
        uid = payload.sliceArray(1..(payload.size-1))
        _rawTlvs = payload
    }

    override fun getPayload(): ByteArray = _rawTlvs

    override fun getCommandCode(): Byte = COMMAND_CODE
}