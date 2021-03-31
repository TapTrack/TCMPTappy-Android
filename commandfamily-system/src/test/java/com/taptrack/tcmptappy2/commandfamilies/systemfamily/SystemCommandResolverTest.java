package com.taptrack.tcmptappy2.commandfamilies.systemfamily;

import androidx.annotation.NonNull;

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
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.GetNVMConfigCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.PingCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetConfigItemCommand;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.SetNVMConfigCommand;
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
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.GetNVMConfigResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.GreenLEDActivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.GreenLEDDeactivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.HardwareVersionResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.ImproperMessageFormatResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.IndicatorStatusResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.LcsMismatchErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.LengthMismatchErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.RedLEDActivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.RedLEDDeactivatedResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.SetNVMConfigResponse;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses.SystemErrorResponse;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SystemCommandResolverTest {
    private SystemCommandResolver library = new SystemCommandResolver();
    private static class FakeCommand extends AbstractSystemMessage {

        @Override
        public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {

        }

        @NonNull
        @Override
        public byte[] getPayload() {
            return new byte[0];
        }

        @Override
        public byte getCommandCode() {
            return 0x76;
        }
    }

    private static class FakeResponse extends AbstractSystemMessage {

        @Override
        public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {

        }

        @NonNull
        @Override
        public byte[] getPayload() {
            return new byte[0];
        }

        @Override
        public byte getCommandCode() {
            return 0x76;
        }
    }
    @Test
    public void testParseCommand() throws Exception {
        assertTrue(testCommandSupported(new ConfigureKioskModeCommand(),ConfigureKioskModeCommand.class));
        assertTrue(testCommandSupported(new GetBatteryLevelCommand(),GetBatteryLevelCommand.class));
        assertTrue(testCommandSupported(new GetFirmwareVersionCommand(),GetFirmwareVersionCommand.class));
        assertTrue(testCommandSupported(new GetHardwareVersionCommand(),GetHardwareVersionCommand.class));
        assertTrue(testCommandSupported(new PingCommand(),PingCommand.class));
        assertTrue(testCommandSupported(new SetConfigItemCommand(),SetConfigItemCommand.class));
        assertTrue(testCommandSupported(new ConfigureOnboardScanCooldownCommand(),ConfigureOnboardScanCooldownCommand.class));
        assertTrue(testCommandSupported(new ActivateRedLEDCommand(), ActivateRedLEDCommand.class));
        assertTrue(testCommandSupported(new ActivateBlueLEDCommand(), ActivateBlueLEDCommand.class));
        assertTrue(testCommandSupported(new ActivateGreenLEDCommand(), ActivateGreenLEDCommand.class));
        assertTrue(testCommandSupported(new ActivateBuzzerCommand(), ActivateBuzzerCommand.class));
        assertTrue(testCommandSupported(new DeactivateRedLEDCommand(), DeactivateRedLEDCommand.class));
        assertTrue(testCommandSupported(new DeactivateBlueLEDCommand(), DeactivateBlueLEDCommand.class));
        assertTrue(testCommandSupported(new DeactivateGreenLEDCommand(), DeactivateGreenLEDCommand.class));
        assertTrue(testCommandSupported(new DeactivateBuzzerCommand(), DeactivateBuzzerCommand.class));
        assertTrue(testCommandSupported(new ActivateRedLEDForDurationCommand(), ActivateRedLEDForDurationCommand.class));
        assertTrue(testCommandSupported(new ActivateBlueLEDForDurationCommand(), ActivateBlueLEDForDurationCommand.class));
        assertTrue(testCommandSupported(new ActivateGreenLEDForDurationCommand(), ActivateGreenLEDForDurationCommand.class));
        assertTrue(testCommandSupported(new ActivateBuzzerForDurationCommand(), ActivateBuzzerForDurationCommand.class));
        assertTrue(testCommandSupported(new GetIndicatorStatusCommand(), GetIndicatorStatusCommand.class));
        assertTrue(testCommandSupported(new DisableHostHeartbeatTransmissionCommand(), DisableHostHeartbeatTransmissionCommand.class));
        assertTrue(testCommandSupported(new GetClockStatusCommand(), GetClockStatusCommand.class));
        assertTrue(testCommandSupported(new GetNVMConfigCommand(), GetNVMConfigCommand.class));
        assertTrue(testCommandSupported(new SetNVMConfigCommand(), SetNVMConfigCommand.class));

        assertFalse(testCommandSupported(new FakeCommand(),FakeCommand.class));
    }

    private boolean testCommandSupported(TCMPMessage message, Class<? extends TCMPMessage> clazz)
            throws MalformedPayloadException {
        TCMPMessage parsedMessage = library.resolveCommand(message);
        if (parsedMessage != null) {
            assertThat(parsedMessage,instanceOf(clazz));
            assertArrayEquals(message.getPayload(), parsedMessage.getPayload());
            return true;
        } else {
            return false;
        }
    }

    @Test
    public void testParseResponse() throws Exception {
        assertTrue(testResponseSupported(new ConfigItemResponse(),ConfigItemResponse.class));
        assertTrue(testResponseSupported(new ConfigureKioskModeResponse(),ConfigureKioskModeResponse.class));
        assertTrue(testResponseSupported(new CrcMismatchErrorResponse(),CrcMismatchErrorResponse.class));
        assertTrue(testResponseSupported(new FirmwareVersionResponse(),FirmwareVersionResponse.class));
        assertTrue(testResponseSupported(new GetBatteryLevelResponse(),GetBatteryLevelResponse.class));
        assertTrue(testResponseSupported(new HardwareVersionResponse(), HardwareVersionResponse.class));
        assertTrue(testResponseSupported(new ImproperMessageFormatResponse(), ImproperMessageFormatResponse.class));
        assertTrue(testResponseSupported(new LcsMismatchErrorResponse(), LcsMismatchErrorResponse.class));
        assertTrue(testResponseSupported(new LengthMismatchErrorResponse(), LengthMismatchErrorResponse.class));
        assertTrue(testResponseSupported(new ConfigureOnboardScanCooldownResponse(), ConfigureOnboardScanCooldownResponse.class));
        assertTrue(testResponseSupported(new SystemErrorResponse(), SystemErrorResponse.class));
        
        assertTrue(testResponseSupported(new RedLEDDeactivatedResponse(), RedLEDDeactivatedResponse.class));
        assertTrue(testResponseSupported(new BlueLEDDeactivatedResponse(), BlueLEDDeactivatedResponse.class));
        assertTrue(testResponseSupported(new GreenLEDDeactivatedResponse(), GreenLEDDeactivatedResponse.class));
        assertTrue(testResponseSupported(new BuzzerDeactivatedResponse(), BuzzerDeactivatedResponse.class));

        assertTrue(testResponseSupported(new RedLEDActivatedResponse(), RedLEDActivatedResponse.class));
        assertTrue(testResponseSupported(new BlueLEDActivatedResponse(), BlueLEDActivatedResponse.class));
        assertTrue(testResponseSupported(new GreenLEDActivatedResponse(), GreenLEDActivatedResponse.class));
        assertTrue(testResponseSupported(new BuzzerActivatedResponse(), BuzzerActivatedResponse.class));
        assertTrue(testResponseSupported(new IndicatorStatusResponse(), IndicatorStatusResponse.class));
        assertTrue(testResponseSupported(new DisableHostHeartbeatTransmissionResponse(), DisableHostHeartbeatTransmissionResponse.class));

        assertTrue(testResponseSupported(new ClockStatusResponse(), ClockStatusResponse.class));

        assertTrue(testResponseSupported(new GetNVMConfigResponse(), GetNVMConfigResponse.class));
        assertTrue(testResponseSupported(new SetNVMConfigResponse(), SetNVMConfigResponse.class));

        assertFalse(testResponseSupported(new FakeResponse(), FakeResponse.class));
    }

    private boolean testResponseSupported(TCMPMessage message,Class<? extends TCMPMessage> clazz)
            throws MalformedPayloadException {
        TCMPMessage parsedMessage = library.resolveResponse(message);
        if (parsedMessage != null) {
            assertThat(parsedMessage,instanceOf(clazz));
            assertArrayEquals(message.getPayload(), parsedMessage.getPayload());
            return true;
        } else {
            return false;
        }
    }

    @Test
    public void testGetCommandFamilyId() throws Exception {
        byte[] familyID = library.getCommandFamilyId();
        assertNotNull(familyID);
        assertArrayEquals(new byte[]{0x00,0x00},familyID);
    }
}
