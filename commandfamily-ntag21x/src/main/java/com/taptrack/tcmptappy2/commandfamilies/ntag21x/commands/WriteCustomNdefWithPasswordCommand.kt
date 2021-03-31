package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import com.taptrack.tcmptappy2.MalformedPayloadException

class WriteCustomNdefWithPasswordCommand : AbstractWriteWithPasswordCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x03
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: ByteArray,
        content: ByteArray,
    ) : super(timeout, readProtectionEnabled, password, content)

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: String,
        content: ByteArray,
    ) : this(timeout, readProtectionEnabled, password.toByteArray(), content)

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super(payload)

    override fun getCommandCode(): Byte = COMMAND_CODE

}
