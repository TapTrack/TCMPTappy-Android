package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import com.taptrack.tcmptappy2.MalformedPayloadException

class ReadNdefWithPasswordCommand : AbstractPasswordCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x04
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(timeout: Byte, password: String) : super(timeout, password)

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super() {
        parsePayload(payload)
    }

    @Throws(MalformedPayloadException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size < 3) {
            throw MalformedPayloadException("Payload too short")
        }

        timeout = payload[0]
        password = payload.sliceArray(3..payload.lastIndex)
    }

    override fun getPayload(): ByteArray = byteArrayOf(
        timeout,
        *passwordLengthBytes,
        *password,
    )

    override fun getCommandCode(): Byte = COMMAND_CODE

}
