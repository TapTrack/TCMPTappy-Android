package com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses

import androidx.annotation.Size
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.AbstractNtag21xMessage
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.toUInt16

abstract class AbstractSuccessResponse : AbstractNtag21xMessage() {

    var tagType: Byte = 0x00
        protected set

    var uid: ByteArray = byteArrayOf()
        protected set

    protected val uidLengthBytes: ByteArray
        @Size(2)
        get() {
            val length = uid.size
            return length.toUInt16()
        }

}
