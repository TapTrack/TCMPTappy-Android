package com.taptrack.experiments.rancheria.ui

import java.util.regex.Pattern
import kotlin.experimental.and

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun Byte.toUnsigned() : Int {
    return (this and 0xff.toByte()).toInt()
}

fun ByteArray.toHex() : String{
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

internal var p = Pattern.compile("[0-9a-fA-F]+")
fun String.isTextValidHex(): Boolean {
    if (this.isEmpty())
        return false
    if (this.length % 2 != 0)
        return false
    val m = p.matcher(this)
    return m.matches()
}

/**
 * Adapted from http://stackoverflow.com/questions/140131
 * *
 * @return byte array
 */
fun String.hexStringToByteArray(): ByteArray {
    val len = this.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}