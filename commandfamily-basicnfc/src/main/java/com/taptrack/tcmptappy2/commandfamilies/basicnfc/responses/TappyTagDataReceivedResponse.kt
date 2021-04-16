package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage

class TappyTagDataReceivedResponse : AbstractBasicNfcMessage {

    companion object {
        const val COMMAND_CODE: Byte = 0x0F
    }

    private var payload = byteArrayOf()

    constructor() : super()

    constructor(payload: ByteArray) {
        parsePayload(payload)
    }

    override fun parsePayload(payload: ByteArray) {
        this.payload = payload
    }

    override fun getPayload(): ByteArray = payload

    override fun getCommandCode() = COMMAND_CODE

}
