package com.taptrack.tcmptappy2.commandfamilies.type4;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.taptrack.tcmptappy2.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.TCMPMessage;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.DetectActiveHCETargetCommand;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.DetectType4BCommand;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.DetectType4BSpecificAfiCommand;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.DetectType4Command;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.GetNextCommandFromActiveReaderInitiatorCommand;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.GetType4LibraryVersionCommand;
import com.taptrack.tcmptappy2.commandfamilies.type4.commands.TransceiveApduCommand;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.APDUTransceiveSuccessfulResponse;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.ActiveHCETargetDetectedResponse;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.ActiveHCETargetRemovedResponse;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.Type4BDetectedResponse;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.Type4DetectedResponse;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.Type4ErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.Type4LibraryVersionResponse;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.Type4PollingErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.type4.responses.Type4TimeoutResponse;

import java.util.Arrays;

/**
 * Command library for Type 4 commands and responses.
 */
public class Type4CommandResolver implements CommandFamilyMessageResolver {
    public static final byte[] FAMILY_ID = new byte[]{0x00,0x04};

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
            case DetectType4Command.COMMAND_CODE:
                parsedMessage = new DetectType4Command();
                break;
            case GetType4LibraryVersionCommand.COMMAND_CODE:
                parsedMessage = new GetType4LibraryVersionCommand();
                break;
            case DetectType4BCommand.COMMAND_CODE:
                parsedMessage = new DetectType4BCommand();
                break;
            case DetectType4BSpecificAfiCommand.COMMAND_CODE:
                parsedMessage = new DetectType4BSpecificAfiCommand();
                break;
            case TransceiveApduCommand.COMMAND_CODE:
                parsedMessage = new TransceiveApduCommand();
                break;
            case DetectActiveHCETargetCommand.COMMAND_CODE:
                parsedMessage = new DetectActiveHCETargetCommand();
                break;
            case DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand.COMMAND_CODE:
                parsedMessage = new DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand();
                break;
            case GetNextCommandFromActiveReaderInitiatorCommand.COMMAND_CODE:
                parsedMessage = new GetNextCommandFromActiveReaderInitiatorCommand();
                break;
            case DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand.COMMAND_CODE:
                parsedMessage = new DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand();
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
            case APDUTransceiveSuccessfulResponse.COMMAND_CODE:
                parsedMessage = new APDUTransceiveSuccessfulResponse();
                break;
            case Type4DetectedResponse.COMMAND_CODE:
                parsedMessage = new Type4DetectedResponse();
                break;
            case Type4ErrorResponse.COMMAND_CODE:
                parsedMessage = new Type4ErrorResponse();
                break;
            case Type4LibraryVersionResponse.COMMAND_CODE:
                parsedMessage = new Type4LibraryVersionResponse();
                break;
            case Type4PollingErrorResponse.COMMAND_CODE:
                parsedMessage = new Type4PollingErrorResponse();
                break;
            case Type4TimeoutResponse.COMMAND_CODE:
                parsedMessage = new Type4TimeoutResponse();
                break;
            case Type4BDetectedResponse.COMMAND_CODE:
                parsedMessage = new Type4BDetectedResponse();
                break;
            case ActiveHCETargetDetectedResponse.COMMAND_CODE:
                parsedMessage = new ActiveHCETargetDetectedResponse();
                break;
            case ActiveHCETargetRemovedResponse.COMMAND_CODE:
                parsedMessage = new ActiveHCETargetRemovedResponse();
            break;
            default:
                return null;
        }
        parsedMessage.parsePayload(message.getPayload());

        return parsedMessage;
    }

    @Override
    @NonNull
    @Size(2)
    public byte[] getCommandFamilyId() {
        return FAMILY_ID;
    }
}
