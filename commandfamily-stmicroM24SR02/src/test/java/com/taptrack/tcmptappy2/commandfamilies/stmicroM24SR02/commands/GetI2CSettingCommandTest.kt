package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import org.junit.Assert
import org.junit.Test

class GetI2CSettingCommandTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = GetI2CSettingCommand()
        Assert.assertEquals(command.commandCode, 0x07.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        var timeout: Byte = 0x05
        val payload = byteArrayOf(timeout)
        val command = GetI2CSettingCommand(timeout)
        Assert.assertArrayEquals(command.payload, payload)
        Assert.assertEquals(command.timeout, timeout)
    }
}