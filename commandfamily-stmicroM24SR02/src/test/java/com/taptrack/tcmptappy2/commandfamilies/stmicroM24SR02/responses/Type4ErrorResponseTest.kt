package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import com.taptrack.tcmptappy2.MalformedPayloadException
import org.junit.Assert
import org.junit.Test

class Type4ErrorResponseTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = Type4ErrorResponse()
        Assert.assertEquals(command.commandCode, 0x02.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        val command = Type4ErrorResponse(byteArrayOf(0x00, 0x01))
        Assert.assertArrayEquals(command.payload, byteArrayOf(0x00, 0x01))
    }

    @Test
    @Throws(Exception::class)
    fun testError() {
        Assert.assertThrows(MalformedPayloadException::class.java) {
            val command = Type4ErrorResponse(byteArrayOf(0x00, 0x01, 0x02))
        }
    }
}