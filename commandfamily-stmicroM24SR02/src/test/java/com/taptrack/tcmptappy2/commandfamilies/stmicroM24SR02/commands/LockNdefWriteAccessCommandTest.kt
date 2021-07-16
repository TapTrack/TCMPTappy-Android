package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import com.taptrack.tcmptappy2.MalformedPayloadException
import org.junit.Assert
import org.junit.Test

class LockNdefWriteAccessCommandTest {
    @Test
    @Throws(Exception::class)
    fun testGetCommandCode() {
        val command = LockNdefWriteAccessCommand()
        Assert.assertEquals(command.commandCode, 0x03.toByte())
    }

    @Test
    @Throws(Exception::class)
    fun testPayload() {
        var timeout: Byte = 0x05
        var p1 = ByteArray(16)
        for(i in 0 until 16){
            p1[i] = i.toByte()
        }
        val payload = byteArrayOf(timeout) + p1
        val command = LockNdefWriteAccessCommand(payload)
        Assert.assertArrayEquals(command.payload, payload)
        Assert.assertEquals(command.timeout, timeout)
        Assert.assertArrayEquals(command.password, p1)
    }

    @Test
    @Throws(Exception::class)
    fun testLogicalParameters() {
        var timeout: Byte = 0x05
        var password = ByteArray(16)
        for(i in 0 until 16){
            password[i] = i.toByte()
        }
        val payload = byteArrayOf(timeout) + password
        val command = LockNdefWriteAccessCommand(timeout, password)
        Assert.assertArrayEquals(command.payload, payload)
        Assert.assertEquals(command.timeout, timeout)
        Assert.assertArrayEquals(command.password, password)
    }

    @Test
    @Throws(Exception::class)
    fun testError(){
        var timeout: Byte = 0x05
        var p1 = ByteArray(17)
        for(i in 0 until 17){
            p1[i] = i.toByte()
        }
        val payload = byteArrayOf(timeout) + p1
        Assert.assertThrows("Payload length isnt 17", MalformedPayloadException::class.java){
            val command = LockNdefWriteAccessCommand(payload)
        }
    }
}