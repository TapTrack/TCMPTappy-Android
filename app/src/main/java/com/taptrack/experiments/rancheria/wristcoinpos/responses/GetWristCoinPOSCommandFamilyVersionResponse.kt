package com.taptrack.experiments.rancheria.wristcoinpos.responses

import com.taptrack.experiments.rancheria.wristcoinpos.AbstractWristCoinPOSMessage
import com.taptrack.tcmptappy2.TCMPMessageParseException

internal class GetWristCoinPOSCommandFamilyVersionResponse : AbstractWristCoinPOSMessage {

    companion object {
        const val COMMAND_CODE: Byte = 0xFF.toByte()
    }

    private var _majorVersion: Byte = 0x00
    private var _minorVersion: Byte = 0x00

    var majorVersion
        get() = _majorVersion
        private set(value) {
            _majorVersion = value
        }

    var minorVersion
        get() = _minorVersion
        private set(value) {
            _minorVersion = value
        }

    constructor() : super()

    @Throws(TCMPMessageParseException::class)
    constructor(payload: ByteArray) : this() {
        parsePayload(payload)
    }

    @Throws(TCMPMessageParseException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size < 2) {
            throw TCMPMessageParseException("Payload too short")
        }

        majorVersion = payload[0]
        minorVersion = payload[1]
    }

    override fun getPayload(): ByteArray = byteArrayOf(majorVersion, minorVersion)

    override fun getCommandCode(): Byte = COMMAND_CODE

}
