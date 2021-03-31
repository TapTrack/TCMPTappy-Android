package com.taptrack.tcmptappy2.commandfamilies.mifareclassic;

import androidx.annotation.NonNull;

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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MifareClassicCommandResolverTest {
    MifareClassicCommandResolver library = new MifareClassicCommandResolver();

    private static class FakeCommand extends AbstractMifareClassicMessage {

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

    private static class FakeResponse extends AbstractMifareClassicMessage {

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
        assertTrue(testCommandSupported(new DetectMifareClassicCommand(),DetectMifareClassicCommand.class));
        assertTrue(testCommandSupported(new GetMifareClassicLibraryVersionCommand(),GetMifareClassicLibraryVersionCommand.class));
        assertTrue(testCommandSupported(new ReadMifareClassicCommand(),ReadMifareClassicCommand.class));

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
        assertTrue(testResponseSupported(new MifareClassicDetectedResponse(), MifareClassicDetectedResponse.class));
        assertTrue(testResponseSupported(new MifareClassicLibraryErrorResponse(), MifareClassicLibraryErrorResponse.class));
        assertTrue(testResponseSupported(new MifareClassicLibraryVersionResponse(), MifareClassicLibraryVersionResponse.class));
        assertTrue(testResponseSupported(new MifareClassicTimeoutResponse(), MifareClassicTimeoutResponse.class));
        assertTrue(testResponseSupported(new MifareClassicReadSuccessResponse(), MifareClassicReadSuccessResponse.class));

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
        assertArrayEquals(library.getCommandFamilyId(),new byte[]{0x00,0x03});
    }
}
