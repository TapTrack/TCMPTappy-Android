package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands

import com.taptrack.kotlin_tlv.*
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage
import com.taptrack.tcmptappy2.pollingTimeout
import com.taptrack.tcmptappy2.tappyTagCustomAid
import com.taptrack.tcmptappy2.tappyTagResponseData
import com.taptrack.tcmptappy2.tappyTagShouldSwitchToKeyboardMode


// TODO: Add constructor with parameters
class InitiateTappyTagHandshakeCommand : AbstractBasicNfcMessage {
    var responseData: ByteArray = byteArrayOf()
    private var customAid: ByteArray = byteArrayOf()
    private var _rawTlvs: ByteArray = byteArrayOf()
    var duration: Byte = 0
    private var shouldSwitchToKeyboardMode : Boolean = false

    companion object {
        const val COMMAND_CODE: Byte = 0x12
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(IllegalArgumentException::class)
    constructor(responseData: ByteArray = byteArrayOf(),
                duration: Int = 0,
                customAid: ByteArray = byteArrayOf(),
                shouldSwitchToKeyboardEmulation: Boolean = false,
                ) : super() {
        var tlvs : MutableList<TLV> = mutableListOf()

        if(duration != 0){
            tlvs.add(TLV(pollingTimeout, byteArrayOf(duration.toByte())))
            this.duration = duration.toByte()
        }

        if(responseData.size in 1..32767){
            tlvs.add(TLV(tappyTagResponseData, responseData))
            this.responseData = responseData
        }else if (responseData.size > 32767){
            throw IllegalArgumentException()
        }

        if(customAid.size in 1..16){
            tlvs.add(TLV(tappyTagCustomAid, customAid))
            this.customAid = customAid
        }else if (customAid.size > 16){
            throw IllegalArgumentException()
        }

        if(shouldSwitchToKeyboardEmulation){
            tlvs.add(TLV(tappyTagShouldSwitchToKeyboardMode, byteArrayOf()))
            shouldSwitchToKeyboardMode = true
        }

        _rawTlvs = tlvs.writeOutTLVBinary()
    }

    @Throws (TLV.MalformedTlvByteArrayException::class)
    override fun parsePayload(payload: ByteArray) {

        if(payload.isEmpty()){
            return
        }
        try {
            var tlvs: List<TLV> = parseTlvData(payload)
            _rawTlvs = payload

            var timeoutValue: ByteArray = fetchTlvValue(tlvs, pollingTimeout)
            if (timeoutValue.size == 1) {
                duration = timeoutValue[0]
            }

            responseData = fetchTlvValue(tlvs, tappyTagResponseData)
            customAid = fetchTlvValue(tlvs, tappyTagCustomAid)

            if (null != lookUpTlvInListIfPresent(tlvs, tappyTagShouldSwitchToKeyboardMode)) {
                shouldSwitchToKeyboardMode = true
            }
        } catch(e: Exception){
            throw TLV.MalformedTlvByteArrayException()
        }
    }

    override fun getPayload(): ByteArray = _rawTlvs // Returns a parsedTlv array as byte array

    override fun getCommandCode() = COMMAND_CODE

}
