package com.taptrack.tcmptappy2.commandfamilies.type4;

import androidx.annotation.NonNull;

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

import org.junit.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class Type4CommandResolverTest {
    Type4CommandResolver library = new Type4CommandResolver();
    Random random = new Random();

    private static class FakeCommand extends AbstractType4Message {

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

    private static class FakeResponse extends AbstractType4Message {

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
        assertTrue(testCommandSupported(new DetectType4Command((byte) 5), DetectType4Command.class));
        assertTrue(testCommandSupported(new DetectActiveHCETargetCommand((byte) 5), DetectActiveHCETargetCommand.class));
        assertTrue(testCommandSupported(new DetectType4BCommand(), DetectType4BCommand.class));
        assertTrue(testCommandSupported(new DetectType4BSpecificAfiCommand(), DetectType4BSpecificAfiCommand.class));
        assertTrue(testCommandSupported(new GetType4LibraryVersionCommand(), GetType4LibraryVersionCommand.class));

        byte[] apdu = new byte[20];
        random.nextBytes(apdu);
        assertTrue(testCommandSupported(new TransceiveApduCommand(apdu), TransceiveApduCommand.class));

        assertTrue(testCommandSupported(new DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand((byte) 5), DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand.class));
        assertTrue(testCommandSupported(new GetNextCommandFromActiveReaderInitiatorCommand(), GetNextCommandFromActiveReaderInitiatorCommand.class));
        assertTrue(testCommandSupported(new DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand((byte) 5), DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand.class));

        assertFalse(testCommandSupported(new FakeCommand(), FakeCommand.class));
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
        byte[] apdu = new byte[20];
        random.nextBytes(apdu);
        assertTrue(testResponseSupported(new APDUTransceiveSuccessfulResponse(apdu), APDUTransceiveSuccessfulResponse.class));

        byte[] uid = new byte[7];
        byte[] ats = new byte[3];
        random.nextBytes(uid);
        random.nextBytes(ats);
        assertTrue(testResponseSupported(new Type4DetectedResponse(uid, ats), Type4DetectedResponse.class));

        byte[] atqb = new byte[255];
        byte[] attrib = new byte[255];
        random.nextBytes(atqb);
        random.nextBytes(attrib);
        assertTrue(testResponseSupported(new Type4BDetectedResponse(atqb,attrib), Type4BDetectedResponse.class));

        assertTrue(testResponseSupported(new Type4ErrorResponse((byte) 0x02,(byte)  0x03,(byte)  0x04, "Test"), Type4ErrorResponse.class));
        assertTrue(testResponseSupported(new Type4LibraryVersionResponse((byte)0x0a,(byte)0x12),Type4LibraryVersionResponse.class));
        assertTrue(testResponseSupported(new Type4PollingErrorResponse(),Type4PollingErrorResponse.class));
        assertTrue(testResponseSupported(new Type4TimeoutResponse(),Type4TimeoutResponse.class));
        assertTrue(testResponseSupported(new ActiveHCETargetRemovedResponse(),ActiveHCETargetRemovedResponse.class));

        byte[] activeHceCmd = new byte[255];
        random.nextBytes(activeHceCmd);
        assertTrue(testResponseSupported(new ActiveHCETargetDetectedResponse(activeHceCmd),ActiveHCETargetDetectedResponse.class));

        assertFalse(testResponseSupported(new FakeResponse(),FakeResponse.class));
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
        assertArrayEquals(library.getCommandFamilyId(), new byte[]{0x00, 0x04});
    }
}
