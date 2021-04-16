package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.toUInt16

abstract class AbstractWriteWithPasswordBytesCommand : AbstractPasswordBytesCommand {

    private var _readProtection: Boolean = false
    private var _content: ByteArray = byteArrayOf()

    var readProtectionEnabled: Boolean
        get() = _readProtection
        protected set(value) {
            _readProtection = value
        }

    var content: ByteArray
        get() = _content
        protected set(value) {
            _content = value
        }

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
        @Size(4) password: ByteArray,
        @Size(2) passwordAcknowledgement: ByteArray,
        content: ByteArray
    ) : super(timeout, password, passwordAcknowledgement) {
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

    @Throws(MalformedPayloadException::class)
    final override fun parsePayload(payload: ByteArray) {
        if (payload.size < 10) {
            throw MalformedPayloadException("Payload too short")
        }

        timeout = payload[0]
        val readProtection = payload[1]

        if (readProtection != 0x00.toByte() && readProtection != 0x01.toByte()) {
            throw MalformedPayloadException(
                "Invalid password protection byte. Expected 0 or 1. Received $readProtection"
            )
        }

        this.readProtectionEnabled = readProtection != 0x00.toByte()
        password = payload.sliceArray(2..5)
        passwordAcknowledgement = payload.sliceArray(6..7)

        if (payload.size > 10) {
            content = payload.sliceArray(10..payload.lastIndex)
        }
    }

    final override fun getPayload(): ByteArray = byteArrayOf(
        timeout,
        if (readProtectionEnabled) 0x01 else 0x00,
        *password,
        *passwordAcknowledgement,
        *contentLengthBytes,
        *content,
    )

}
