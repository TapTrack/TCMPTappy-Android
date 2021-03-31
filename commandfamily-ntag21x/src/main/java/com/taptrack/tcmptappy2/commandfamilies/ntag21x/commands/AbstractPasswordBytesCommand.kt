package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage

abstract class AbstractPasswordBytesCommand : AbstractNtag21xMessage {

    private var _timeout: Byte = 0x00

    @Size(4)
    private var _password: ByteArray = ByteArray(4)

    @Size(2)
    private var _passwordAcknowledgement: ByteArray = ByteArray(2)

    var timeout: Byte
        get() = _timeout
        protected set(value) {
            _timeout = value
        }

    var password: ByteArray
        @Size(4)
        get() = _password
        protected set(@Size(4) value) {
            _password = value
        }

    var passwordAcknowledgement: ByteArray
        @Size(2)
        get() = _passwordAcknowledgement
        protected set(@Size(2) value) {
            _passwordAcknowledgement = value
        }

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
