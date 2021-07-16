package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses

import com.taptrack.kotlin_tlv.TLV
import com.taptrack.kotlin_tlv.fetchTlvValue
import com.taptrack.kotlin_tlv.parseTlvData
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage
import com.taptrack.tcmptappy2.pollingTimeout
import com.taptrack.tcmptappy2.tappyTagInitialHandshakeData

class TappyTagDataReceivedResponse : AbstractBasicNfcMessage {
    private var rawTlvs = byteArrayOf()
    var dataReceived = byteArrayOf()
    var timeout = byteArrayOf()

    companion object {
        const val COMMAND_CODE: Byte = 0x0F
    }

    constructor() : super()

    constructor(payload: ByteArray) {
        parsePayload(payload)
    }

    @Throws(TLV.MalformedTlvByteArrayException::class)
    override fun parsePayload(payload: ByteArray) {
        var tlvs: List<TLV> = parseTlvData(payload)
        this.rawTlvs = payload
        dataReceived = fetchTlvValue(tlvs, tappyTagInitialHandshakeData)
        timeout = fetchTlvValue(tlvs, pollingTimeout)
    }

    override fun getPayload(): ByteArray = rawTlvs

    override fun getCommandCode() = COMMAND_CODE

}
