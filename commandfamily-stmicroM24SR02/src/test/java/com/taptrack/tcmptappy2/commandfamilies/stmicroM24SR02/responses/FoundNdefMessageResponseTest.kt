package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import com.taptrack.kotlin_tlv.ByteUtils
import org.junit.Assert
import org.junit.Test

class FoundNdefMessageResponseTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = FoundNdefMessageResponse()
        Assert.assertEquals(command.commandCode, 0x0A.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        val responseCode: Byte = 0x0A
        val length = 14
        var uid = byteArrayOf(0x00, 0x04, 0x0F, 0x05, 0x01, 0x0F, 0x08, 0x0A, 0x0D, 0x04, 0x05, 0x0B, 0x08, 0x00)
        val ndefLength = 1000
        var message = ByteArray(1000)
        for(i in 0 until length){
            message[i] = 0x00
        }

        val payload = byteArrayOf(length.toByte()) + uid + ByteUtils.intToArray(ndefLength).sliceArray(2..3) + message
        val response = FoundNdefMessageResponse(payload)
        Assert.assertArrayEquals(response.payload, payload)
        Assert.assertArrayEquals(response.uid, uid)
        Assert.assertArrayEquals(response.content, message)
    }
}