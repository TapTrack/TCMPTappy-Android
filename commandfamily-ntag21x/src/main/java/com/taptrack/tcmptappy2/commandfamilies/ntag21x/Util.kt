@file:JvmName("Util")

package com.taptrack.tcmptappy2.commandfamilies.ntag21x

fun Int.toUInt16(): ByteArray {
    val high = (this shr 8).toByte()
    val low = (this and 0xFF).toByte()
    return byteArrayOf(high, low)
}

fun ByteArray.asUInt16ToInt(): Int {
    val high = (this[0].toInt() and 0xFF) shl 8
    val low = this[1].toInt() and 0xFF
    return high or low
}
