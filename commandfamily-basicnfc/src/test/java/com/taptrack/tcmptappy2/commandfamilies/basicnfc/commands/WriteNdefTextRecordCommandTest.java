package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.LockingModes;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefTextRecordCommand;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WriteNdefTextRecordCommandTest {
    private byte[] generateTestPayload(byte timeout, byte lockTag, byte[] text) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(timeout);
        byteArrayOutputStream.write(lockTag);
        try {
            byteArrayOutputStream.write(text);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte timeout = 0x03;
        byte lockTag = LockingModes.LOCK_TAG;
        String text = "Test String";
        byte[] textBytes = text.getBytes();
        byte[] payload = generateTestPayload(timeout,lockTag,textBytes);

        WriteNdefTextRecordCommand recordCommand = new WriteNdefTextRecordCommand();
        recordCommand.parsePayload(payload);
        assertEquals(timeout, recordCommand.getTimeout());
        assertEquals(lockTag,recordCommand.getLockflag());
        assertEquals(lockTag == LockingModes.LOCK_TAG,recordCommand.willLock());
        assertArrayEquals(recordCommand.getTextBytes(), textBytes);
        assertEquals(text,recordCommand.getText());
    }

    @Test
    public void testGetPayload() throws Exception {
        byte timeout = 0x03;
        byte lockTag = LockingModes.LOCK_TAG;
        String text = "Test String";
        byte[] textBytes = text.getBytes();
        byte[] payload = generateTestPayload(timeout, lockTag, textBytes);

        WriteNdefTextRecordCommand recordCommand = new WriteNdefTextRecordCommand(timeout,lockTag,text);
        assertArrayEquals(payload,recordCommand.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        WriteNdefTextRecordCommand command = new WriteNdefTextRecordCommand();
        assertEquals(command.getCommandCode(),(byte)0x06);
    }
}