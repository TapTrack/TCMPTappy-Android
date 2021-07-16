package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin;

import com.taptrack.tcmptappy2.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.TCMPMessage;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.GetCheckinCountCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.GetCheckinsCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.GetStandaloneCheckinFamilyVersionCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.GetStationInfoCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.GetTimeAndDateCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.ReadCheckinCardUidCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.ResetCheckinsCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.SetStationIdCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.SetStationNameCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands.SetTimeAndDateCommand;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.CheckinDataResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.CheckinTagUidResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.CheckinsResetResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.NoCheckinsPresentResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.NumberOfCheckinsResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.StandaloneCheckinErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.StandaloneCheckinLibraryVersionResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.StationIdSetSuccessResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.StationInfoResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.StationNameSetSuccessResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.TagDetectionTimedOut;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.TimeAndDateSetResponse;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses.TimeAndDateResponse;

public class StandaloneCheckinCommandResolver implements CommandFamilyMessageResolver {
    public static final byte[] FAMILY_ID = new byte[]{0x00,0x05};
    @Override
    public TCMPMessage resolveCommand(TCMPMessage message) throws MalformedPayloadException {
        TCMPMessage parsedMessage;
        switch(message.getCommandCode()) {
            case GetCheckinsCommand.COMMAND_CODE:
                parsedMessage = new GetCheckinsCommand();
                break;
            case GetCheckinCountCommand.COMMAND_CODE:
                parsedMessage = new GetCheckinCountCommand();
                break;
            case ReadCheckinCardUidCommand.COMMAND_CODE:
                parsedMessage = new ReadCheckinCardUidCommand();
                break;
            case SetStationIdCommand.COMMAND_CODE:
                parsedMessage = new SetStationIdCommand();
                break;
            case SetStationNameCommand.COMMAND_CODE:
                parsedMessage = new SetStationNameCommand();
                break;
            case ResetCheckinsCommand.COMMAND_CODE:
                parsedMessage = new ResetCheckinsCommand();
                break;
            case GetStationInfoCommand.COMMAND_CODE:
                parsedMessage = new GetStationInfoCommand();
                break;
            case SetTimeAndDateCommand.COMMAND_CODE:
                parsedMessage = new SetTimeAndDateCommand();
                break;
            case GetTimeAndDateCommand.COMMAND_CODE:
                parsedMessage = new GetTimeAndDateCommand();
                break;
            case GetStandaloneCheckinFamilyVersionCommand.COMMAND_CODE:
                parsedMessage = new GetStandaloneCheckinFamilyVersionCommand();
                break;
            default:
                return null;
        }
        parsedMessage.parsePayload(message.getPayload());

        return parsedMessage;
    }

    @Override
    public TCMPMessage resolveResponse(TCMPMessage message) throws MalformedPayloadException {
        TCMPMessage parsedMessage;
        switch(message.getCommandCode()) {
            case CheckinDataResponse.COMMAND_CODE:
                parsedMessage = new CheckinDataResponse();
                break;
            case StationIdSetSuccessResponse.COMMAND_CODE:
                parsedMessage = new StationIdSetSuccessResponse();
                break;
            case NumberOfCheckinsResponse.COMMAND_CODE:
                parsedMessage = new NumberOfCheckinsResponse();
                break;
            case StationNameSetSuccessResponse.COMMAND_CODE:
                parsedMessage = new StationNameSetSuccessResponse();
                break;
            case CheckinTagUidResponse.COMMAND_CODE:
                parsedMessage = new CheckinTagUidResponse();
                break;
            case StationInfoResponse.COMMAND_CODE:
                parsedMessage = new StationInfoResponse();
                break;
            case CheckinsResetResponse.COMMAND_CODE:
                parsedMessage = new CheckinsResetResponse();
                break;
            case NoCheckinsPresentResponse.COMMAND_CODE:
                parsedMessage = new NoCheckinsPresentResponse();
                break;
            case TimeAndDateResponse.COMMAND_CODE:
                parsedMessage = new TimeAndDateResponse();
                break;
            case TimeAndDateSetResponse.COMMAND_CODE:
                parsedMessage = new TimeAndDateSetResponse();
                break;
            case TagDetectionTimedOut.COMMAND_CODE:
                parsedMessage = new TagDetectionTimedOut();
                break;

            case StandaloneCheckinLibraryVersionResponse.COMMAND_CODE:
                parsedMessage = new StandaloneCheckinLibraryVersionResponse();
                break;
            case StandaloneCheckinErrorResponse.COMMAND_CODE:
                parsedMessage = new StandaloneCheckinErrorResponse();
                break;
            default:
                return null;
        }
        parsedMessage.parsePayload(message.getPayload());

        return parsedMessage;
    }

    @Override
    public byte[] getCommandFamilyId() {
        return FAMILY_ID;
    }
}
