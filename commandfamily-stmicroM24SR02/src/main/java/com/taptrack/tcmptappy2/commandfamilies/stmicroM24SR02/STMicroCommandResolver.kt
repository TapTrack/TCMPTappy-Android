package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02

import com.taptrack.tcmptappy2.CommandFamilyMessageResolver
import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.TCMPMessage
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands.*
import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.responses.*

class STMicroCommandResolver : CommandFamilyMessageResolver {
    companion object{

        val FAMILY_ID = byteArrayOf(0x00, 0x08)

        @Throws(IllegalArgumentException::class)
        private fun assertFamilyMatches(message: TCMPMessage) {
            if (!message.commandFamily.contentEquals(FAMILY_ID)) {
                throw IllegalArgumentException("Specified message is for a different command family")
            }
        }
    }

    @Throws(MalformedPayloadException::class)
    override fun resolveCommand(message: TCMPMessage): TCMPMessage? {
        assertFamilyMatches(message)

        var parsedMessage: TCMPMessage

        when(message.commandCode) {
            ChangeReadNdefPasswordCommand.COMMAND_CODE -> {
                parsedMessage = ChangeReadNdefPasswordCommand()
            }
            ChangeWriteNdefPasswordCommand.COMMAND_CODE -> {
                parsedMessage = ChangeWriteNdefPasswordCommand()
            }
            GetCommandFamilyVersionCommand.COMMAND_CODE -> {
                parsedMessage = GetCommandFamilyVersionCommand()
            }
            GetI2CSettingCommand.COMMAND_CODE -> {
                parsedMessage = GetI2CSettingCommand()
            }
            LockNdefReadAccessCommand.COMMAND_CODE -> {
                parsedMessage = LockNdefReadAccessCommand()
            }
            LockNdefWriteAccessCommand.COMMAND_CODE -> {
                parsedMessage = LockNdefWriteAccessCommand()
            }
            UnlockNdefReadAccessCommand.COMMAND_CODE -> {
                parsedMessage = UnlockNdefReadAccessCommand()
            }
            UnlockNdefWriteAccessCommand.COMMAND_CODE -> {
                parsedMessage = UnlockNdefWriteAccessCommand()
            }
            PermanentlyLockNdefWriteAccessCommand.COMMAND_CODE -> {
                parsedMessage = PermanentlyLockNdefWriteAccessCommand()
            }
            ReadNdefMsgWithPasswordCommand.COMMAND_CODE -> {
                parsedMessage = ReadNdefMsgWithPasswordCommand()
            }
            WriteNdefWithPasswordCommand.COMMAND_CODE -> {
                parsedMessage = WriteNdefWithPasswordCommand()
            }
            else -> {
                return null
            }
        }

        parsedMessage.parsePayload(message.payload)
        return parsedMessage
    }

    @Throws(MalformedPayloadException::class)
    override fun resolveResponse(message: TCMPMessage): TCMPMessage? {
        assertFamilyMatches(message)
        val parsedMessage: TCMPMessage
        when (message.commandCode) {
            ChangeReadNdefPasswordResponse.COMMAND_CODE -> {
                parsedMessage = ChangeReadNdefPasswordResponse()
            }
            ChangeWriteNdefPasswordResponse.COMMAND_CODE -> {
                parsedMessage = ChangeWriteNdefPasswordResponse()
            }
            GetCommandFamilyVersionResponse.COMMAND_CODE -> {
                parsedMessage = GetCommandFamilyVersionResponse()
            }
            GetI2CProtectionSettingResponse.COMMAND_CODE -> {
                parsedMessage = GetI2CProtectionSettingResponse()
            }
            PasswordLockNdefReadResponse.COMMAND_CODE -> {
                parsedMessage = PasswordLockNdefReadResponse()
            }
            PasswordLockNdefWriteResponse.COMMAND_CODE -> {
                parsedMessage = PasswordLockNdefWriteResponse()
            }
            PasswordUnlockNdefReadResponse.COMMAND_CODE -> {
                parsedMessage = PasswordUnlockNdefReadResponse()
            }
            PasswordUnlockNdefWriteResponse.COMMAND_CODE -> {
                parsedMessage = PasswordUnlockNdefWriteResponse()
            }
            PermanentNdefWriteLockResponse.COMMAND_CODE -> {
                parsedMessage = PermanentNdefWriteLockResponse()
            }
            FoundNdefMessageResponse.COMMAND_CODE -> {
                parsedMessage = FoundNdefMessageResponse()
            }
            NdefMessageWrittenResponse.COMMAND_CODE -> {
                parsedMessage = NdefMessageWrittenResponse()
            }
            PollingTimeoutResponse.COMMAND_CODE -> {
                parsedMessage = PollingTimeoutResponse()
            }
            Type4ErrorResponse.COMMAND_CODE -> {
                parsedMessage = Type4ErrorResponse()
            }
            else -> {
                return null
            }
        }
        parsedMessage.parsePayload(message.payload)
        return parsedMessage
    }

    override fun getCommandFamilyId(): ByteArray {
        return FAMILY_ID
    }
}