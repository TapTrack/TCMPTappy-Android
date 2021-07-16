package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import org.junit.Assert
import org.junit.Test

class PermanentNdefWriteLockResponseTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = PermanentNdefWriteLockResponse()
        Assert.assertEquals(command.commandCode, 0x0C.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        val length = 14
        var uid = byteArrayOf(0x00, 0x04, 0x0F, 0x05, 0x01, 0x0F, 0x08, 0x0A, 0x0D, 0x04, 0x05, 0x0B, 0x08, 0x00)

        val payload = byteArrayOf(length.toByte()) + uid
        val response = PermanentNdefWriteLockResponse(payload)
        Assert.assertArrayEquals(response.payload, payload)
        Assert.assertArrayEquals(response.uid, uid)
    }
}