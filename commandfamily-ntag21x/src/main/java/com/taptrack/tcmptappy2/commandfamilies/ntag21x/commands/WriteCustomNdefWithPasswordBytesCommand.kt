package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.MalformedPayloadException

class WriteCustomNdefWithPasswordBytesCommand : AbstractWriteWithPasswordBytesCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x07
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        @Size(4) password: ByteArray,
        @Size(2) passwordAcknowledgement: ByteArray,
        content: ByteArray
    ) : super(timeout, readProtectionEnabled, password, passwordAcknowledgement, content)

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super(payload)

    override fun getCommandCode(): Byte = COMMAND_CODE

}
