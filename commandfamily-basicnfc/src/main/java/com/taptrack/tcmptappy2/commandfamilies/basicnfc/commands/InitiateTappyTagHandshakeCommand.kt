package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage

// TODO: Add constructor with parameters
class InitiateTappyTagHandshakeCommand : AbstractBasicNfcMessage {

    companion object {
        const val COMMAND_CODE: Byte = 0x12
    }

    constructor() : super()

    constructor(payload: ByteArray) {
        parsePayload(payload)
    }

    override fun parsePayload(payload: ByteArray) = Unit

    override fun getPayload(): ByteArray = byteArrayOf()

    override fun getCommandCode() = COMMAND_CODE

}
