package com.taptrack.tcmptappy2.commandfamilies.ntag21x

import androidx.annotation.Size
import com.taptrack.tcmptappy2.CommandFamilyMessageResolver
import com.taptrack.tcmptappy2.MalformedPayloadException
import com.taptrack.tcmptappy2.TCMPMessage
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.commands.*
import com.taptrack.tcmptappy2.commandfamilies.ntag21x.responses.*

class Ntag21xCommandResolver : CommandFamilyMessageResolver {

    companion object {
        @JvmField
        val FAMILY_ID = byteArrayOf(0x00, 0x06)

        @Throws(IllegalArgumentException::class)
        private fun assertFamilyMatches(message: TCMPMessage) {
            if (!message.commandFamily.contentEquals(FAMILY_ID)) {
                throw IllegalArgumentException("Specified message is for a different command family")
            }
        }
    }

    @Throws(IllegalArgumentException::class, MalformedPayloadException::class)
    override fun resolveCommand(message: TCMPMessage): TCMPMessage? {
        assertFamilyMatches(message)

        val parsedMessage: TCMPMessage = when (message.commandCode) {
            WriteTextNdefWithPasswordCommand.COMMAND_CODE -> {
                WriteTextNdefWithPasswordCommand()
            }
            WriteUriNdefWithPasswordCommand.COMMAND_CODE -> {
                WriteUriNdefWithPasswordCommand()
            }
            WriteCustomNdefWithPasswordCommand.COMMAND_CODE -> {
                WriteCustomNdefWithPasswordCommand()
            }
            ReadNdefWithPasswordCommand.COMMAND_CODE -> {
                ReadNdefWithPasswordCommand()
            }
            WriteTextNdefWithPasswordBytesCommand.COMMAND_CODE -> {
                WriteTextNdefWithPasswordBytesCommand()
            }
            WriteUriNdefWithPasswordBytesCommand.COMMAND_CODE -> {
                WriteUriNdefWithPasswordBytesCommand()
            }
            WriteCustomNdefWithPasswordBytesCommand.COMMAND_CODE -> {
                WriteCustomNdefWithPasswordBytesCommand()
            }
            ReadNdefWithPasswordBytesCommand.COMMAND_CODE -> {
                ReadNdefWithPasswordBytesCommand()
            }
            GetNtag21xCommandFamilyVersionCommand.COMMAND_CODE -> {
                GetNtag21xCommandFamilyVersionCommand()
            }
            else -> {
                return null
            }
        }

        parsedMessage.parsePayload(message.payload)
        return parsedMessage
    }

    @Throws(IllegalArgumentException::class, MalformedPayloadException::class)
    override fun resolveResponse(message: TCMPMessage): TCMPMessage? {
        assertFamilyMatches(message)

        val parsedMessage: TCMPMessage = when (message.commandCode) {
            Ntag21xReadSuccessResponse.COMMAND_CODE -> {
                Ntag21xReadSuccessResponse()
            }
            Ntag21xPollingTimeoutResponse.COMMAND_CODE -> {
                Ntag21xPollingTimeoutResponse()
            }
            GetNtag21xCommandFamilyVersionResponse.COMMAND_CODE -> {
                GetNtag21xCommandFamilyVersionResponse()
            }
            Ntag21xWriteSuccessResponse.COMMAND_CODE -> {
                Ntag21xWriteSuccessResponse()
            }
            Ntag21xApplicationErrorResponse.COMMAND_CODE -> {
                Ntag21xApplicationErrorResponse()
            }
            else -> {
                return null
            }
        }

        parsedMessage.parsePayload(message.payload)
        return parsedMessage
    }

    @Size(2)
    override fun getCommandFamilyId(): ByteArray = FAMILY_ID

}
