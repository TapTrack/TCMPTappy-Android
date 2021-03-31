package com.taptrack.tcmptappy2.commandfamilies.mifareclassic;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.TCMPMessage;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.GetMifareClassicLibraryVersionCommand;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses.MifareClassicDetectedResponse;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses.MifareClassicLibraryErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses.MifareClassicLibraryVersionResponse;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses.MifareClassicReadSuccessResponse;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses.MifareClassicTimeoutResponse;

import java.util.Arrays;

/**
 * Command family for MIFARE Classic commands
 */
public class MifareClassicCommandResolver implements CommandFamilyMessageResolver {
    public static final byte[] FAMILY_ID = new byte[]{0x00,0x03};

    private static void assertFamilyMatches(@NonNull TCMPMessage message) {
        if (!Arrays.equals(message.getCommandFamily(),FAMILY_ID)) {
            throw new IllegalArgumentException("Specified message is for a different command family");
        }
    }

    @Override
    public TCMPMessage resolveCommand(@NonNull TCMPMessage message) throws MalformedPayloadException {
        assertFamilyMatches(message);

        TCMPMessage parsedMessage;
        switch(message.getCommandCode()) {
            case DetectMifareClassicCommand.COMMAND_CODE:
                parsedMessage = new DetectMifareClassicCommand();
                break;
            case GetMifareClassicLibraryVersionCommand.COMMAND_CODE:
                parsedMessage = new GetMifareClassicLibraryVersionCommand();
                break;
            case ReadMifareClassicCommand.COMMAND_CODE:
                parsedMessage = new ReadMifareClassicCommand();
                break;
            default:
                return null;
        }
        parsedMessage.parsePayload(message.getPayload());

        return parsedMessage;
    }

    @Override
    public TCMPMessage resolveResponse(@NonNull TCMPMessage message) throws MalformedPayloadException {
        assertFamilyMatches(message);

        TCMPMessage parsedMessage;
        switch(message.getCommandCode()) {
            case MifareClassicDetectedResponse.COMMAND_CODE:
                parsedMessage = new MifareClassicDetectedResponse();
                break;
            case MifareClassicLibraryErrorResponse.COMMAND_CODE:
                parsedMessage = new MifareClassicLibraryErrorResponse();
                break;
            case MifareClassicLibraryVersionResponse.COMMAND_CODE:
                parsedMessage = new MifareClassicLibraryVersionResponse();
                break;
            case MifareClassicTimeoutResponse.COMMAND_CODE:
                parsedMessage = new MifareClassicTimeoutResponse();
                break;
            case MifareClassicReadSuccessResponse.COMMAND_CODE:
                parsedMessage = new MifareClassicReadSuccessResponse();
                break;
            default:
                return null;
        }
        parsedMessage.parsePayload(message.getPayload());

        return parsedMessage;
    }

    @NonNull
    @Override
    public byte[] getCommandFamilyId() {
        return FAMILY_ID;
    }
}
