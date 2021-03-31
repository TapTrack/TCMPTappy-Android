package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.LockingModes;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefUriRecordCommand;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WriteNdefUriRecordCommandTest {
    private byte[] generateTestPayload(byte timeout, byte lockTag, byte uriFlag, byte[] uri) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(timeout);
        byteArrayOutputStream.write(lockTag);
        byteArrayOutputStream.write(uriFlag);
        try {
            byteArrayOutputStream.write(uri);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte timeout = 0x03;
        byte lockTag = LockingModes.LOCK_TAG;
        byte uriCode = NdefUriCodes.URICODE_HTTPWWW;
        String uri = "taptrack.com";
        byte[] uriBytes = uri.getBytes();
        byte[] payload = generateTestPayload(timeout,lockTag,uriCode,uriBytes);

        WriteNdefUriRecordCommand recordCommand = new WriteNdefUriRecordCommand();
        recordCommand.parsePayload(payload);
        assertEquals(timeout, recordCommand.getTimeout());
        assertEquals(lockTag, recordCommand.getLockflag());
        assertEquals(uriCode,recordCommand.getUriCode());
        assertEquals(lockTag == LockingModes.LOCK_TAG,recordCommand.willLock());
        assertEquals(uri, recordCommand.getUri());
        assertArrayEquals(uriBytes, recordCommand.getUriBytes());
    }

    @Test
    public void testGetPayload() throws Exception {
        byte timeout = 0x03;
        byte lockTag = LockingModes.LOCK_TAG;
        byte uriCode = NdefUriCodes.URICODE_HTTPWWW;
        String uri = "taptrack.com";
        byte[] uriBytes = uri.getBytes();
        byte[] payload = generateTestPayload(timeout, lockTag, uriCode, uriBytes);


        WriteNdefUriRecordCommand recordCommand =
                new WriteNdefUriRecordCommand(timeout,lockTag,uriCode,uriBytes);
        assertArrayEquals(payload, recordCommand.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        WriteNdefUriRecordCommand command = new WriteNdefUriRecordCommand();
        assertEquals(command.getCommandCode(),0x05);
    }
}