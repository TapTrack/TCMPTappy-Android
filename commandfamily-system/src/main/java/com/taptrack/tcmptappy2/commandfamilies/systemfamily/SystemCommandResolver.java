package com.taptrack.tcmptappy2.commandfamilies.systemfamily;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.taptrack.tcmptappy2.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.TCMPMessage;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ActivateBlueLEDCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ActivateBlueLEDForDurationCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ActivateBuzzerCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ActivateBuzzerForDurationCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ActivateGreenLEDCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ActivateGreenLEDForDurationCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ActivateRedLEDCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ActivateRedLEDForDurationCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ConfigureKioskModeCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ConfigureOnboardScanCooldownCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.DeactivateBlueLEDCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.DeactivateBuzzerCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.DeactivateGreenLEDCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.DeactivateRedLEDCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.DisableHostHeartbeatTransmissionCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.GetBatteryLevelCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.GetClockStatusCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.GetFirmwareVersionCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.GetHardwareVersionCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.GetIndicatorStatusCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.GetBootConfigCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.PingCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetConfigItemCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetBootConfigCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.BlueLEDActivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.BlueLEDDeactivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.BuzzerActivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.BuzzerDeactivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.ClockStatusResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.ConfigItemResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.ConfigureKioskModeResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.ConfigureOnboardScanCooldownResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.CrcMismatchErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.DisableHostHeartbeatTransmissionResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.FirmwareVersionResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.GetBatteryLevelResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.GetBootConfigResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.GreenLEDActivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.GreenLEDDeactivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.HardwareVersionResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.ImproperMessageFormatResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.IndicatorStatusResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.LcsMismatchErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.LengthMismatchErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.PingResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.RedLEDActivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.RedLEDDeactivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.SetBootConfigResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.SystemErrorResponse;

import java.util.Arrays;

public class SystemCommandResolver implements CommandFamilyMessageResolver {
    public static final byte[] FAMILY_ID = new byte[]{0x00,0x00};

    private static void assertFamilyMatches(@NonNull TCMPMessage message) {
        if (!Arrays.equals(message.getCommandFamily(),FAMILY_ID)) {
            throw new IllegalArgumentException("Specified message is for a different command family");
        }
    }

    @Override
    @Nullable
    public TCMPMessage resolveCommand(@NonNull TCMPMessage message) throws MalformedPayloadException {
        assertFamilyMatches(message);

        TCMPMessage parsedMessage;
        switch (message.getCommandCode()) {
            case GetHardwareVersionCommand.COMMAND_CODE:
                parsedMessage = new GetHardwareVersionCommand();
                break;

            case GetFirmwareVersionCommand.COMMAND_CODE:
                parsedMessage = new GetFirmwareVersionCommand();
                break;

            case GetBatteryLevelCommand.COMMAND_CODE:
                parsedMessage = new GetBatteryLevelCommand();
                break;

            case GetClockStatusCommand.COMMAND_CODE:
                parsedMessage = new GetClockStatusCommand();
                break;

            case PingCommand.COMMAND_CODE:
                parsedMessage = new PingCommand();
                break;

            case SetConfigItemCommand.COMMAND_CODE:
                parsedMessage = new SetConfigItemCommand();
                break;

            case ConfigureKioskModeCommand.COMMAND_CODE:
                parsedMessage = new ConfigureKioskModeCommand();
                break;

            case ConfigureOnboardScanCooldownCommand.COMMAND_CODE:
                parsedMessage = new ConfigureOnboardScanCooldownCommand();
                break;

            case ActivateBlueLEDCommand.COMMAND_CODE:
                parsedMessage = new ActivateBlueLEDCommand();
                break;

            case ActivateRedLEDCommand.COMMAND_CODE:
                parsedMessage = new ActivateRedLEDCommand();
                break;

            case ActivateGreenLEDCommand.COMMAND_CODE:
                parsedMessage = new ActivateGreenLEDCommand();
                break;

            case ActivateBuzzerCommand.COMMAND_CODE:
                parsedMessage = new ActivateBuzzerCommand();
                break;

            case DeactivateBlueLEDCommand.COMMAND_CODE:
                parsedMessage = new DeactivateBlueLEDCommand();
                break;

            case DeactivateRedLEDCommand.COMMAND_CODE:
                parsedMessage = new DeactivateRedLEDCommand();
                break;

            case DeactivateGreenLEDCommand.COMMAND_CODE:
                parsedMessage = new DeactivateGreenLEDCommand();
                break;

            case DeactivateBuzzerCommand.COMMAND_CODE:
                parsedMessage = new DeactivateBuzzerCommand();
                break;

            case ActivateBlueLEDForDurationCommand.COMMAND_CODE:
                parsedMessage = new ActivateBlueLEDForDurationCommand();
                break;

            case ActivateRedLEDForDurationCommand.COMMAND_CODE:
                parsedMessage = new ActivateRedLEDForDurationCommand();
                break;

            case ActivateGreenLEDForDurationCommand.COMMAND_CODE:
                parsedMessage = new ActivateGreenLEDForDurationCommand();
                break;

            case ActivateBuzzerForDurationCommand.COMMAND_CODE:
                parsedMessage = new ActivateBuzzerForDurationCommand();
                break;

            case GetIndicatorStatusCommand.COMMAND_CODE:
                parsedMessage = new GetIndicatorStatusCommand();
                break;

            case DisableHostHeartbeatTransmissionCommand.COMMAND_CODE:
                parsedMessage = new DisableHostHeartbeatTransmissionCommand();
                break;

            case GetBootConfigCommand.COMMAND_CODE:
                parsedMessage = new GetBootConfigCommand();
                break;

            case SetBootConfigCommand.COMMAND_CODE:
                parsedMessage = new SetBootConfigCommand();
                break;

            default:
                return null;
        }

        parsedMessage.parsePayload(message.getPayload());
        return parsedMessage;
    }

    @Override
    @Nullable
    public TCMPMessage resolveResponse(@NonNull TCMPMessage message) throws MalformedPayloadException {
        assertFamilyMatches(message);

        TCMPMessage parsedMessage;
        switch (message.getCommandCode()) {
            case ConfigItemResponse.COMMAND_CODE:
                parsedMessage = new ConfigItemResponse();
                break;

            case ConfigureKioskModeResponse.COMMAND_CODE:
                parsedMessage = new ConfigureKioskModeResponse();
                break;

            case CrcMismatchErrorResponse.COMMAND_CODE:
                parsedMessage = new CrcMismatchErrorResponse();
                break;

            case FirmwareVersionResponse.COMMAND_CODE:
                parsedMessage = new FirmwareVersionResponse();
                break;

            case GetBatteryLevelResponse.COMMAND_CODE:
                parsedMessage = new GetBatteryLevelResponse();
                break;

            case HardwareVersionResponse.COMMAND_CODE:
                parsedMessage = new HardwareVersionResponse();
                break;

            case ClockStatusResponse.COMMAND_CODE:
                parsedMessage = new ClockStatusResponse();
                break;

            case ImproperMessageFormatResponse.COMMAND_CODE:
                parsedMessage = new ImproperMessageFormatResponse();
                break;

            case LcsMismatchErrorResponse.COMMAND_CODE:
                parsedMessage = new LcsMismatchErrorResponse();
                break;

            case LengthMismatchErrorResponse.COMMAND_CODE:
                parsedMessage = new LengthMismatchErrorResponse();
                break;

            case PingResponse.COMMAND_CODE:
                parsedMessage = new PingResponse();
                break;

            case SystemErrorResponse.COMMAND_CODE:
                parsedMessage = new SystemErrorResponse();
                break;

            case ConfigureOnboardScanCooldownResponse.COMMAND_CODE:
                parsedMessage = new ConfigureOnboardScanCooldownResponse();
                break;

            case BlueLEDActivatedResponse.COMMAND_CODE:
                parsedMessage = new BlueLEDActivatedResponse();
                break;

            case RedLEDActivatedResponse.COMMAND_CODE:
                parsedMessage = new RedLEDActivatedResponse();
                break;

            case GreenLEDActivatedResponse.COMMAND_CODE:
                parsedMessage = new GreenLEDActivatedResponse();
                break;

            case BuzzerActivatedResponse.COMMAND_CODE:
                parsedMessage = new BuzzerActivatedResponse();
                break;

            case BlueLEDDeactivatedResponse.COMMAND_CODE:
                parsedMessage = new BlueLEDDeactivatedResponse();
                break;

            case RedLEDDeactivatedResponse.COMMAND_CODE:
                parsedMessage = new RedLEDDeactivatedResponse();
                break;

            case GreenLEDDeactivatedResponse.COMMAND_CODE:
                parsedMessage = new GreenLEDDeactivatedResponse();
                break;

            case BuzzerDeactivatedResponse.COMMAND_CODE:
                parsedMessage = new BuzzerDeactivatedResponse();
                break;

            case IndicatorStatusResponse.COMMAND_CODE:
                parsedMessage = new IndicatorStatusResponse();
                break;

            case DisableHostHeartbeatTransmissionResponse.COMMAND_CODE:
                parsedMessage = new DisableHostHeartbeatTransmissionResponse();
                break;

            case GetBootConfigResponse.COMMAND_CODE:
                parsedMessage = new GetBootConfigResponse();
                break;

            case SetBootConfigResponse.COMMAND_CODE:
                parsedMessage = new SetBootConfigResponse();
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
