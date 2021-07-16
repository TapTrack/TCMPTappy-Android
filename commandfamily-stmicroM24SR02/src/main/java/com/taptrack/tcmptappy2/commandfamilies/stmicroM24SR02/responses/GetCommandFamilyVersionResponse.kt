package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class GetCommandFamilyVersionResponse : AbstractStMicroMessage {
    var minorVersion: Byte = 0x00
    var majorVersion: Byte = 0x00
    private var _rawTlvs: ByteArray = byteArrayOf()

    companion object {
        const val COMMAND_CODE: Byte = 0xFF.toByte()
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    override fun parsePayload(payload: ByteArray) {
        majorVersion = payload[0]
        minorVersion = payload[1]
        _rawTlvs = payload
    }

    override fun getPayload(): ByteArray = _rawTlvs

    override fun getCommandCode(): Byte = COMMAND_CODE
}