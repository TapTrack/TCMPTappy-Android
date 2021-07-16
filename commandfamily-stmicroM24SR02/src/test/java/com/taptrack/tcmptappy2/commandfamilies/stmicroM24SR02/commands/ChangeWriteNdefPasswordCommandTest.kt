package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import com.taptrack.tcmptappy2.MalformedPayloadException
import org.junit.Assert
import org.junit.Test

class ChangeWriteNdefPasswordCommandTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = ChangeWriteNdefPasswordCommand()
        Assert.assertEquals(command.commandCode, 0x02.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        var timeout: Byte = 0x05
        var p1 = ByteArray(16)
        var p2 = ByteArray(16)
        for(i in 0 until 16){
            p1[i] = i.toByte()
        }
        for(i in 0 until 16){
            p2[i] = 0x00
        }
        val payload = byteArrayOf(timeout) + p1 + p2
        val command = ChangeWriteNdefPasswordCommand(payload)
        Assert.assertArrayEquals(command.payload, payload)
        Assert.assertEquals(command.timeout, timeout)
        Assert.assertArrayEquals(command.currentPassword, p1)
        Assert.assertArrayEquals(command.newPassword, p2)
    }

    @Test
    @Throws(Exception::class)
    fun testLogicalParameters() {
        var timeout: Byte = 0x05
        var p1 = ByteArray(16)
        var p2 = ByteArray(16)
        for(i in 0 until 16){
            p1[i] = i.toByte()
        }
        for(i in 0 until 16){
            p2[i] = 0x00
        }
        val payload = byteArrayOf(timeout) + p1 + p2
        val command = ChangeWriteNdefPasswordCommand(timeout, p1, p2)
        Assert.assertArrayEquals(command.payload, payload)
        Assert.assertEquals(command.timeout, timeout)
        Assert.assertArrayEquals(command.currentPassword, p1)
        Assert.assertArrayEquals(command.newPassword, p2)
    }

    @Test
    @Throws(Exception::class)
    fun testError(){
        var timeout: Byte = 0x05
        var p1 = ByteArray(16)
        var p2 = ByteArray(17)
        for(i in 0 until 16){
            p1[i] = i.toByte()
        }
        for(i in 0 until 17){
            p2[i] = 0x00
        }
        val payload = byteArrayOf(timeout) + p1 + p2
        Assert.assertThrows("Payload length isnt 33", MalformedPayloadException::class.java){
            val command = ChangeWriteNdefPasswordCommand(payload)
        }
    }
}