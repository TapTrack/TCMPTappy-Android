package com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses

import androidx.annotation.Size
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.toUInt16

abstract class AbstractSuccessResponse : AbstractNtag21xMessage() {

    private var _tagType: Byte = 0x00
    private var _uid: ByteArray = byteArrayOf()

    var tagType: Byte
        get() = _tagType
        protected set(value) {
            _tagType = value
        }

    var uid: ByteArray
        get() = _uid
        protected set(value) {
            _uid = value
        }

    protected val uidLengthBytes: ByteArray
        @Size(2)
        get() {
            val length = uid.size
            return length.toUInt16()
        }

}
