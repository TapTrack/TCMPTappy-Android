package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses

import org.junit.Assert
import org.junit.Test

class GetI2CProtectionSettingResponseTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = GetI2CProtectionSettingResponse()
        Assert.assertEquals(command.commandCode, 0x09.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        val protec: Byte = 0x00
        val length = 14
        var uid = byteArrayOf(0x00, 0x04, 0x0F, 0x05, 0x01, 0x0F, 0x08, 0x0A, 0x0D, 0x04, 0x05, 0x0B, 0x08, 0x00)

        val payload = byteArrayOf(protec, length.toByte()) + uid
        val response = GetI2CProtectionSettingResponse(payload)
        Assert.assertArrayEquals(response.payload, payload)
        Assert.assertEquals(response.protecSetting, protec)
        Assert.assertArrayEquals(response.uid, uid)
    }

}