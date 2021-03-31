package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.MalformedPayloadException

class ReadNdefWithPasswordBytesCommand : AbstractPasswordBytesCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x08
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        @Size(4) password: ByteArray,
        @Size(2) passwordAcknowledgement: ByteArray
    ) : super(timeout, password, passwordAcknowledgement)

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : this() {
        parsePayload(payload)
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size < 7) {
            throw MalformedPayloadException("Payload too short")
        }

        timeout = payload[0]
        password = payload.sliceArray(1..4)
        passwordAcknowledgement = payload.sliceArray(5..6)
    }

    override fun getPayload(): ByteArray = byteArrayOf(
        timeout,
        *password,
        *passwordAcknowledgement
    )

    override fun getCommandCode(): Byte = COMMAND_CODE

}
