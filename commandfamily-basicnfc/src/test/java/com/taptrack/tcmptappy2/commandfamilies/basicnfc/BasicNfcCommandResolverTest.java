package com.taptrack.tcmptappy2.commandfamilies.basicnfc;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy.commandfamilies.basicnfc.BuildConfig;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.TCMPMessage;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.AutoPollCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.AutoPollNdefCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.DispatchTagCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.DispatchTagsCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.EmulateCustomNdefRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.EmulateTextRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.EmulateUriRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.GetBasicNfcLibraryVersionCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.LockTagCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.ScanNdefCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.ScanTagCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StopCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StreamNdefCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StreamTagsCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefCustomMessageCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefTextRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefUriRecordCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.AutoPollTagEnteredResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.AutoPollTagExitedResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.BasicNfcErrorResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.BasicNfcLibraryVersionResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.EmulationStoppedResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.EmulationSuccessResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.NdefFoundResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ScanTimeoutResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.SignedTagFoundResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TagFoundResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TagLockedResponse;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.TagWrittenResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BasicNfcCommandResolverTest {
    BasicNfcCommandResolver library = new BasicNfcCommandResolver();

    private static class FakeCommand extends AbstractBasicNfcMessage {

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

    private static class FakeResponse extends AbstractBasicNfcMessage {

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
        assertTrue(testCommandSupported(new GetBasicNfcLibraryVersionCommand(),GetBasicNfcLibraryVersionCommand.class));
        assertTrue(testCommandSupported(new ScanNdefCommand(), ScanNdefCommand.class));
        assertTrue(testCommandSupported(new ScanTagCommand(), ScanTagCommand.class));
        assertTrue(testCommandSupported(new StopCommand(), StopCommand.class));
        assertTrue(testCommandSupported(new StreamNdefCommand(), StreamNdefCommand.class));
        assertTrue(testCommandSupported(new StreamTagsCommand(), StreamTagsCommand.class));
        assertTrue(testCommandSupported(new WriteNdefCustomMessageCommand(), WriteNdefCustomMessageCommand.class));
        assertTrue(testCommandSupported(new WriteNdefTextRecordCommand(), WriteNdefTextRecordCommand.class));
        assertTrue(testCommandSupported(new WriteNdefUriRecordCommand(), WriteNdefUriRecordCommand.class));
        assertTrue(testCommandSupported(new LockTagCommand(), LockTagCommand.class));
        assertTrue(testCommandSupported(new DispatchTagCommand(), DispatchTagCommand.class));
        assertTrue(testCommandSupported(new DispatchTagsCommand(), DispatchTagsCommand.class));
        assertTrue(testCommandSupported(new AutoPollCommand(), AutoPollCommand.class));
        assertTrue(testCommandSupported(new AutoPollNdefCommand(), AutoPollNdefCommand.class));
        assertTrue(testCommandSupported(new EmulateTextRecordCommand(), EmulateTextRecordCommand.class));
        assertTrue(testCommandSupported(new EmulateUriRecordCommand(), EmulateUriRecordCommand.class));
        assertTrue(testCommandSupported(new EmulateCustomNdefRecordCommand(), EmulateCustomNdefRecordCommand.class));

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
        assertTrue(testResponseSupported(new BasicNfcErrorResponse(),BasicNfcErrorResponse.class));
        assertTrue(testResponseSupported(new BasicNfcLibraryVersionResponse(),BasicNfcLibraryVersionResponse.class));
        assertTrue(testResponseSupported(new NdefFoundResponse(),NdefFoundResponse.class));
        assertTrue(testResponseSupported(new ScanTimeoutResponse(),ScanTimeoutResponse.class));
        assertTrue(testResponseSupported(new TagFoundResponse(),TagFoundResponse.class));
        assertTrue(testResponseSupported(new SignedTagFoundResponse(), SignedTagFoundResponse.class));
        assertTrue(testResponseSupported(new TagWrittenResponse(),TagWrittenResponse.class));
        assertTrue(testResponseSupported(new TagLockedResponse(), TagLockedResponse.class));
        assertTrue(testResponseSupported(new AutoPollTagExitedResponse(), AutoPollTagExitedResponse.class));
        assertTrue(testResponseSupported(new AutoPollTagEnteredResponse(), AutoPollTagEnteredResponse.class));
        assertTrue(testResponseSupported(new EmulationStoppedResponse(), EmulationStoppedResponse.class));
        assertTrue(testResponseSupported(new EmulationSuccessResponse(), EmulationSuccessResponse.class));

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
        assertArrayEquals(library.getCommandFamilyId(),new byte[]{0x00,0x01});
    }
}
