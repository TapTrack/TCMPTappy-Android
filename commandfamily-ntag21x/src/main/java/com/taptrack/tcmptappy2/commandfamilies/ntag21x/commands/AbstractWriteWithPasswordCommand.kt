package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.asUInt16ToInt
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.toUInt16

abstract class AbstractWriteWithPasswordCommand : AbstractPasswordCommand {

    var readProtectionEnabled = false
        protected set

    var content = byteArrayOf()
        protected set

    private val contentLengthBytes: ByteArray
        @Size(2)
        get() {
            val length = content.size
            return length.toUInt16()
        }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: ByteArray,
        content: ByteArray
    ) : super(timeout, password) {
        if (content.size > 0xFFFF) {
            throw IllegalArgumentException("Content too long")
        }

        this.content = content
        this.readProtectionEnabled = readProtectionEnabled
    }

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) {
        parsePayload(payload)
    }

    final override fun parsePayload(payload: ByteArray) {
        if (payload.size < 6) {
            throw MalformedPayloadException("Payload too short")
        }

        timeout = payload[0]

        val passwordProtectionByte = payload[1]

        if (passwordProtectionByte != 0x00.toByte() && passwordProtectionByte != 0x01.toByte()) {
            throw MalformedPayloadException(
                "Invalid password protection byte. Expected 0 or 1. Received $passwordProtectionByte"
            )
        }

        this.readProtectionEnabled = passwordProtectionByte != 0x00.toByte()

        val passwordLength = payload.sliceArray(2..3).asUInt16ToInt()

        // 6 = 1 (timeout byte)
        //   + 1 (protection byte)
        //   + 2 (password length bytes)
        //   + 2 (content length bytes)
        if (payload.size - 6 < passwordLength) {
            throw MalformedPayloadException("Payload too short")
        }

        val passwordLastIndex = (4 + passwordLength) - 1
        password = payload.sliceArray(4..passwordLastIndex)

        val contentStartIndex = passwordLastIndex + 1
        content = payload.sliceArray(contentStartIndex..payload.lastIndex)
    }

    final override fun getPayload(): ByteArray = byteArrayOf(
        timeout,
        if (readProtectionEnabled) 0x01 else 0x00,
        *passwordLengthBytes,
        *password,
        *contentLengthBytes,
        *content,
    )

}
