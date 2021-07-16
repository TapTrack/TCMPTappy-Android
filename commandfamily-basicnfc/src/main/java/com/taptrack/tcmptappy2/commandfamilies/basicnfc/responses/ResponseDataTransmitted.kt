package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses

import com.taptrack.kotlin_tlv.ByteUtils.arrayToInt
import com.taptrack.kotlin_tlv.TLV
import com.taptrack.kotlin_tlv.fetchTlvValue
import com.taptrack.kotlin_tlv.parseTlvData
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage
import com.taptrack.tcmptappy2.tappyTagResponseDataAckLength
import com.taptrack.tcmptappy2.tappyTagResponseDataAckOffset

class ResponseDataTransmitted  : AbstractBasicNfcMessage {
    var rawTlvs = byteArrayOf()
    var dataOffset : Int = 0
    var dataLength : Int = 0

    companion object {
        const val COMMAND_CODE: Byte = 0x10
    }

    constructor() : super()

    constructor(payload: ByteArray) {
        dataOffset = 0
        dataLength = 0
        parsePayload(payload)
    }

    @Throws (TLV.MalformedTlvByteArrayException::class)
    override fun parsePayload(payload: ByteArray) {
        var tlvs : List<TLV> = parseTlvData(payload)
        rawTlvs = payload

        val offsetValue = fetchTlvValue(tlvs, tappyTagResponseDataAckOffset)
        if(offsetValue.size == 1 || offsetValue.size == 2 || offsetValue.size == 4){
            dataOffset = arrayToInt(byteArrayOf(0, 0) + offsetValue)
        }

        val lengthValue = fetchTlvValue(tlvs, tappyTagResponseDataAckLength)
        if(lengthValue.size == 1 || lengthValue.size == 2 || lengthValue.size == 4){
            dataLength = arrayToInt(byteArrayOf(0, 0) + lengthValue)
        }
    }

    override fun getPayload(): ByteArray = rawTlvs

    override fun getCommandCode() = COMMAND_CODE


}