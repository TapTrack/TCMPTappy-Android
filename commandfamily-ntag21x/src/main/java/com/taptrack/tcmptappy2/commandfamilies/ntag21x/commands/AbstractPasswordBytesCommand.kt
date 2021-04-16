package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage

abstract class AbstractPasswordBytesCommand : AbstractNtag21xMessage {

    @Size(2)
    private var _passwordAcknowledgement: ByteArray = ByteArray(2)

    var timeout: Byte = 0x00
        protected set

    @Size(4)
    var password = ByteArray(4)
        protected set

    @Size(2)
    var passwordAcknowledgement= ByteArray(2)
        protected set

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(
        timeout: Byte,
        @Size(4) password: ByteArray,
        @Size(2) passwordAcknowledgement: ByteArray
    ) : this() {
        if (password.size != 4) {
            throw IllegalArgumentException("Password must be 4 bytes")
        }

        if (passwordAcknowledgement.size != 2) {
            throw IllegalArgumentException("Password acknowledgement must be 2 bytes")
        }

        this.timeout = timeout
        this.password = password
        this.passwordAcknowledgement = passwordAcknowledgement
    }

}
