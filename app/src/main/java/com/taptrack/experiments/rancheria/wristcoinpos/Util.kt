@file:JvmName("Util")

package com.taptrack.experiments.rancheria.wristcoinpos

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

const val UUID_SIZE_BYTES: Long = 16

fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(Int.SIZE_BYTES).apply {
    order(ByteOrder.BIG_ENDIAN)
    putInt(this@toByteArray)
}.array()

fun UUID.toByteArray(): ByteArray = ByteBuffer.allocate(UUID_SIZE_BYTES.toInt()).apply {
    order(ByteOrder.BIG_ENDIAN)
    putLong(mostSignificantBits)
    putLong(leastSignificantBits)
}.array()

fun ByteArray.toInt(startIndex: Int = 0): Int = ByteBuffer.wrap(this).getInt(startIndex)
