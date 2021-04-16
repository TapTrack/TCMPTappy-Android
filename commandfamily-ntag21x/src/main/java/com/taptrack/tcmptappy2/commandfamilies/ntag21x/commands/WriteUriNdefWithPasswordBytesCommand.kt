package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.MalformedPayloadException

class WriteUriNdefWithPasswordBytesCommand : AbstractWriteWithPasswordBytesCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x06
    }

    var uriCode: Byte
        get() = content[0]
        set(value) {
            content[0] = value
        }

    var uri: String
        get() = content.decodeToString(startIndex = 1)
        set(value) {
            content = byteArrayOf(uriCode, *value.toByteArray())
        }

    constructor() : super() {
        content = byteArrayOf(0x00)
    }

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtection: Boolean,
        @Size(4) password: ByteArray,
        @Size(2) passwordAcknowledgement: ByteArray,
        uriCode: Byte,
        uri: ByteArray,
    ) : super(
        timeout,
        readProtection,
        password,
        passwordAcknowledgement,
        byteArrayOf(uriCode, *uri)
    )

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtection: Boolean,
        @Size(4) password: ByteArray,
        @Size(2) passwordAcknowledgement: ByteArray,
        uriCode: Byte,
        uri: String,
    ) : this(
        timeout,
        readProtection,
        password,
        passwordAcknowledgement,
        uriCode,
        uri.toByteArray()
    )

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super(payload)

    override fun getCommandCode(): Byte = COMMAND_CODE

}
