package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import org.junit.Assert
import org.junit.Test

class GetComandFamilyVersionResponseTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = GetCommandFamilyVersionResponse()
        Assert.assertEquals(command.commandCode, 0xFF.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        val majorVersion: Byte = 0x01
        val minorVersion: Byte = 0x00

        val payload = byteArrayOf(majorVersion, minorVersion)
        val response = GetCommandFamilyVersionResponse(payload)
        Assert.assertArrayEquals(response.payload, payload)
        Assert.assertEquals(response.majorVersion, majorVersion)
        Assert.assertEquals(response.minorVersion, minorVersion)
    }
}