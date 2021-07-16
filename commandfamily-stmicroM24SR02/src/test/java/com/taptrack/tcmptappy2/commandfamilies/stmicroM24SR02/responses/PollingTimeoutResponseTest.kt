package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import org.junit.Assert
import org.junit.Test

class PollingTimeoutResponseTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = PollingTimeoutResponse()
        Assert.assertEquals(command.commandCode, 0x01.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        val command = PollingTimeoutResponse()
        Assert.assertArrayEquals(command.payload, byteArrayOf())
    }
}