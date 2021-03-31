package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

class WriteUriNdefWithPasswordCommand : AbstractWriteWithPasswordCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x02
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
        readProtectionEnabled: Boolean,
        password: ByteArray,
        uriCode: Byte,
        uri: ByteArray
    ) : super(timeout, readProtectionEnabled, password, byteArrayOf(uriCode, *uri))

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: ByteArray,
        uriCode: Byte,
        uri: String
    ) : this(timeout, readProtectionEnabled, password, uriCode, uri.toByteArray())

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: String,
        uriCode: Byte,
        uri: ByteArray
    ) : this(timeout, readProtectionEnabled, password.toByteArray(), uriCode, uri)

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        readProtectionEnabled: Boolean,
        password: String,
        uriCode: Byte,
        uri: String
    ) : this(timeout, readProtectionEnabled, password.toByteArray(), uriCode, uri.toByteArray())

    override fun getCommandCode(): Byte = COMMAND_CODE

}
