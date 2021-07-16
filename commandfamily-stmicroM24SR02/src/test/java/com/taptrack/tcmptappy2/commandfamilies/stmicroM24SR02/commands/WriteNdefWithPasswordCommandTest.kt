package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import com.taptrack.kotlin_tlv.ByteUtils.intToArray
import org.junit.Assert
import org.junit.Test

class WriteNdefWithPasswordCommandTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = WriteNdefWithPasswordCommand()
        Assert.assertEquals(command.commandCode, 0x09.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        var timeout: Byte = 0x05
        var p1 = ByteArray(16)
        for(i in 0 until 16){
            p1[i] = i.toByte()
        }
        val length = 1000
        var message = ByteArray(1000)
        for(i in 0 until length){
            message[i] = 0x00
        }

        val payload = byteArrayOf(timeout) + p1 + intToArray(length).sliceArray(2..3) + message
        val command = WriteNdefWithPasswordCommand(payload)
        Assert.assertArrayEquals(command.payload, payload)
        Assert.assertEquals(command.timeout, timeout)
        Assert.assertArrayEquals(command.password, p1)
        Assert.assertArrayEquals(command.length, intToArray(length).sliceArray(2..3))
        Assert.assertArrayEquals(command.content, message)
    }

    @Test
    @Throws(Exception::class)
    fun testLogicalParameters() {
        var timeout: Byte = 0x05
        var password = ByteArray(16)
        for(i in 0 until 16){
            password[i] = i.toByte()
        }
        val length = 1000
        var message = ByteArray(1000)
        for(i in 0 until length){
            message[i] = 0x00
        }
        val payload = byteArrayOf(timeout) + password + intToArray(length).sliceArray(2..3) + message
        val command = WriteNdefWithPasswordCommand(timeout, password, message)
        Assert.assertArrayEquals(command.payload, payload)
        Assert.assertEquals(command.timeout, timeout)
        Assert.assertArrayEquals(command.password, password)
        Assert.assertArrayEquals(command.length, intToArray(length).sliceArray(2..3))
        Assert.assertArrayEquals(command.content, message)
    }
}