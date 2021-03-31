package com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands

import androidx.annotation.Size
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.toUInt16

abstract class AbstractPasswordCommand : AbstractNtag21xMessage {

    private var _timeout: Byte = 0x00
    private var _password: ByteArray = byteArrayOf()

    var timeout: Byte
        get() = _timeout
        protected set(value) {
            _timeout = value
        }

    var password: ByteArray
        get() = _password
        protected set(value) {
            _password = value
        }

    val passwordString: String
        get() = password.decodeToString()

    protected val passwordLengthBytes: ByteArray
        @Size(2)
        get() {
            val length = password.size
            return length.toUInt16()
        }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(timeout: Byte, password: String) : this(timeout, password.toByteArray())

    @Throws(IllegalArgumentException::class)
    constructor(timeout: Byte, password: ByteArray) : this() {
        if (password.size > 0xFFFF) {
            throw IllegalArgumentException("Password too long")
        }

        this.timeout = timeout
        this.password = password
    }

}
