package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class GetI2CProtectionSettingResponse : AbstractStMicroMessage {

    var protecSetting: Byte = 0x00
    var uid: ByteArray = byteArrayOf()
    private var _rawTlvs: ByteArray = byteArrayOf()

    companion object {
        const val COMMAND_CODE: Byte = 0x09
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(IllegalArgumentException::class)
    constructor(protecSetting: Byte = 0x00, uid: ByteArray = byteArrayOf()) : super() {
        this.protecSetting = protecSetting
        this.uid = uid
        _rawTlvs = byteArrayOf(protecSetting, uid.size.toByte()) + uid
    }

    override fun parsePayload(payload: ByteArray) {
        protecSetting = payload[0]
        uid = payload.sliceArray(2..(payload.size-1))
        _rawTlvs = payload
    }

    override fun getPayload(): ByteArray = _rawTlvs

    override fun getCommandCode(): Byte = COMMAND_CODE
}