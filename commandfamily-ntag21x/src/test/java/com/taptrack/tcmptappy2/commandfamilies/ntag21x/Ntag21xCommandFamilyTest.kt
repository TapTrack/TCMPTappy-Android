package com.taptrack.tcmptappy2.commandfamilies.ntag21x

import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands.*
import junit.framework.Assert.*
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class Ntag21xCommandFamilyTest {
    @Test
    fun passwordBytesCommand_ThrowsWithIncorrectPasswordSize() {
        assertThrows(IllegalArgumentException::class.java) {
            WriteCustomNdefWithPasswordBytesCommand(
                timeout = 0,
                readProtectionEnabled = false,
                password = byteArrayOf(),
                passwordAcknowledgement = byteArrayOf(0x00, 0x00),
                content = byteArrayOf()
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            WriteCustomNdefWithPasswordBytesCommand(
                timeout = 0,
                readProtectionEnabled = false,
                password = byteArrayOf(0x00, 0x00, 0x00, 0x00),
                passwordAcknowledgement = byteArrayOf(),
                content = byteArrayOf()
            )
        }
    }

    @Test
    fun passwordCommand_ParsePayloadThrowsWithIncorrectPasswordLength() {
        assertThrows(MalformedPayloadException::class.java) {
            val payload = byteArrayOf(
                0x00,       // timeout
                0x00,       // protection
                0x00, 0x01, // password length = 1
                0x00, 0x00, // content length = 0
            )

            WriteTextNdefWithPasswordCommand(payload)
        }

        val payload = byteArrayOf(
            0x00,         // timeout
            0x00,         // protection
            0x00, 0x01,   // password length = 1
            'y'.toByte(),
            0x00, 0x00,   // content length = 0
        )

        WriteTextNdefWithPasswordCommand(payload)
    }

    @Test
    fun writeWithPasswordCommand_GetReadProtectionReturnsExpectedValue() {
        val first = WriteTextNdefWithPasswordCommand(
            timeout = 0,
            readProtectionEnabled = true,
            password = "",
            text = "",
        )

        assertTrue(first.readProtectionEnabled)
        assertEquals(0x01, first.payload[1])

        val second = WriteTextNdefWithPasswordCommand(
            timeout = 0,
            readProtectionEnabled = false,
            password = "",
            text = "",
        )

        assertFalse(second.readProtectionEnabled)
        assertEquals(0x00, second.payload[1])
    }

    @Test
    fun writeTextNdefWithPasswordBytesCommand_GetTextReturnsExpectedValue() {
        val expected = "Hello, world!"

        val command = WriteTextNdefWithPasswordBytesCommand(
            timeout = 0,
            readProtectionEnabled = false,
            password = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()),
            passwordAcknowledgement = byteArrayOf(0x00, 0x00),
            text = expected
        )

        assertEquals(expected, command.text)
    }

    @Test
    fun writeTextNdefWithPasswordBytesCommand_SetTextProducesExpectedValue() {
        val expected = "Hello, world!"

        val command = WriteTextNdefWithPasswordBytesCommand(
            timeout = 0,
            readProtectionEnabled = false,
            password = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()),
            passwordAcknowledgement = byteArrayOf(0x00, 0x00),
            text = ""
        )

        assertNotEquals(expected, command.text)

        command.text = expected

        assertEquals(expected, command.text)
    }

    @Test
    fun writeTextNdefWithPasswordCommand_GetPasswordStringReturnsExpectedValue() {
        val expected = "password123"

        val command = WriteTextNdefWithPasswordCommand(
            timeout = 0,
            readProtectionEnabled = false,
            password = expected,
            text = ""
        )

        assertEquals(expected, command.passwordString)
    }

    @Test
    fun writeUriNdefCommand_SetUriUpdatesValue() {
        val expected = "google.com"

        val command = WriteUriNdefWithPasswordCommand(
            timeout = 0,
            readProtectionEnabled = false,
            password = "",
            uriCode = 0x00,
            uri = "bing.com"
        )

        val originalUriCode = command.uriCode
        assertNotEquals(expected, command.uri)

        command.uri = expected

        val nextUriCode = command.uriCode
        assertEquals(originalUriCode, nextUriCode)
        assertEquals(expected, command.uri)
    }

    @Test
    fun writeUriNdefCommand_SetUriCodeUpdatesValue() {
        val expected: Byte = 0x01

        val command = WriteUriNdefWithPasswordCommand(
            timeout = 0,
            readProtectionEnabled = false,
            password = "",
            uriCode = 0x00,
            uri = "bing.com"
        )

        val originalUri = command.uri
        assertNotEquals(expected, command.uriCode)

        command.uriCode = expected

        val nextUri = command.uri
        assertEquals(originalUri, nextUri)
        assertEquals(expected, command.uriCode)
    }

    @Test
    fun writeTextNdefWithPasswordCommand_DefaultConstructedShouldBeWellFormed() {
        val command = WriteTextNdefWithPasswordCommand()

        val text = command.text

        assertTrue(text.isEmpty())
    }

    @Test
    fun writeUriNdefWithPasswordCommand_DefaultConstructedShouldBeWellFormed() {
        val command = WriteUriNdefWithPasswordCommand()

        val uriCode = command.uriCode
        val uri = command.uri

        assertEquals(0x00, uriCode)
        assertTrue(uri.isEmpty())
    }

    @Test
    fun writeUriNdefWithPasswordBytesCommand_DefaultConstructedShouldBeWellFormed() {
        val command = WriteUriNdefWithPasswordBytesCommand()

        val uriCode = command.uriCode
        val uri = command.uri

        assertEquals(0x00, uriCode)
        assertTrue(uri.isEmpty())
    }

    @Test
    fun writeNdefCommand_ThrowsIfContentTooLong() {
        assertThrows(IllegalArgumentException::class.java) {
            WriteTextNdefWithPasswordCommand(
                timeout = 0,
                readProtectionEnabled = false,
                password = "password123",
                text = ByteArray(0x10000)
            )
        }

        WriteTextNdefWithPasswordCommand(
            timeout = 0,
            readProtectionEnabled = false,
            password = "password123",
            text = ByteArray(0xFFFF)
        )
    }
}
