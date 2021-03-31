package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.MalformedPayloadException

class WriteTextNdefWithPasswordBytesCommand : AbstractWriteWithPasswordBytesCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x05
    }

    var text: String
        get() = content.decodeToString()
        set(value) {
            content = value.toByteArray()
        }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        @Size(4) password: ByteArray,
        @Size(2) passwordAcknowledgement: ByteArray,
        text: ByteArray
    ) : super(timeout, readProtectionEnabled, password, passwordAcknowledgement, text)

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        @Size(4) password: ByteArray,
        @Size(2) passwordAcknowledgement: ByteArray,
        text: String
    ) : super(timeout, readProtectionEnabled, password, passwordAcknowledgement, text.toByteArray())

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super(payload)

    override fun getCommandCode(): Byte = COMMAND_CODE

}
