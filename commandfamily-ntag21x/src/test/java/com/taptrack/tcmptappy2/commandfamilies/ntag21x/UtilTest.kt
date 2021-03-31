package com.taptrack.tcmptappy2.commandfamilies.ntag21x

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {
    @Test
    fun intToUInt16_ProducesExpectedSequence() {
        val expected = byteArrayOf(0xFF.toByte(), 0xFF.toByte())

        val initial = 65535
        val bytes = initial.toUInt16()

        assertArrayEquals(expected, bytes)
    }

    @Test
    fun intToUInt16_ProducesBigEndianSequence() {
        val expected = byteArrayOf(0x01, 0x02)

        val initial = 258
        val bytes = initial.toUInt16()

        assertArrayEquals(expected, bytes)
    }

    @Test
    fun byteArrayAsUInt16ToInt_ProducesExpectedValue() {
        val expected = 65535

        val initial = byteArrayOf(0xFF.toByte(), 0xFF.toByte())
        val result = initial.asUInt16ToInt()

        assertEquals(expected, result)
    }

    @Test
    fun byteArrayAsUInt16ToInt_InterpretedAsBigEndian() {
        val expected = 258

        val initial = byteArrayOf(0x01, 0x02)
        val result = initial.asUInt16ToInt()

        assertEquals(expected, result)
    }
}
