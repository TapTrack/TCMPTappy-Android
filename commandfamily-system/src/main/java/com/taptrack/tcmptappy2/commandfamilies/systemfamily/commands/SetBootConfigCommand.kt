package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage

// TODO: Provide convenience constructors and accessors accepting user-friendly TLV structure
class SetBootConfigCommand : AbstractSystemMessage {

    companion object {
        const val COMMAND_CODE: Byte = 0x18
    }

    // TODO: When user-friendly TLV structures are supported, consider using this as a backing field
    private var _rawTlvs: ByteArray = byteArrayOf()

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        _rawTlvs = payload
    }

    override fun getPayload(): ByteArray = _rawTlvs

    override fun getCommandCode(): Byte = COMMAND_CODE

}
