package com.taptrack.experiments.rancheria.wristcoinpos.commands

import com.taptrack.experiments.rancheria.wristcoinpos.AbstractWristCoinPOSMessage

internal class CloseoutWristbandCommand : AbstractWristCoinPOSMessage {
    companion object {
        const val COMMAND_CODE: Byte = 0x07
    }

    var timeout: Byte? = null

    constructor() : super()

    constructor(payload: ByteArray) : this() {
        parsePayload(payload)
    }

    constructor(timeout: Byte) : this() {
        this.timeout = timeout
    }

    override fun parsePayload(payload: ByteArray) {
        if (payload.isNotEmpty()) {
            timeout = payload[0]
        }
    }

    override fun getPayload(): ByteArray {
        val localTimeout = timeout

        return if (localTimeout == null) {
            byteArrayOf()
        } else {
            byteArrayOf(localTimeout)
        }
    }

    override fun getCommandCode(): Byte = COMMAND_CODE
}