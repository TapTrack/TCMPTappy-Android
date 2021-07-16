package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import org.junit.Assert
import org.junit.Test

class GetCommandFamilyVersionCommandTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = GetCommandFamilyVersionCommand()
        Assert.assertEquals(command.commandCode, 0xFE.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        val command = GetCommandFamilyVersionCommand()
        Assert.assertArrayEquals(command.payload, byteArrayOf())
    }
}