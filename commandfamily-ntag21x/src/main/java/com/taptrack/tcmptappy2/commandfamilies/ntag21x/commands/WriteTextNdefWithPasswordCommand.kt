package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import com.taptrack.tcmptappy2.MalformedPayloadException

class WriteTextNdefWithPasswordCommand : AbstractWriteWithPasswordCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x01
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
        password: ByteArray,
        text: ByteArray,
    ) : super(timeout, readProtectionEnabled, password, text)

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: ByteArray,
        text: String,
    ) : this(timeout, readProtectionEnabled, password, text.toByteArray())

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: String,
        text: ByteArray,
    ) : this(timeout, readProtectionEnabled, password.toByteArray(), text)

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: String,
        text: String,
    ) : this(timeout, readProtectionEnabled, password.toByteArray(), text.toByteArray())

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super(payload)

    override fun getCommandCode(): Byte = COMMAND_CODE

}
