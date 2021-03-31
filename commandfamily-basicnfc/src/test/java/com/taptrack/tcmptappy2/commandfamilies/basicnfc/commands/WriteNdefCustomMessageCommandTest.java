package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.WriteNdefCustomMessageCommand;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WriteNdefCustomMessageCommandTest {
    Random random = new Random();

    private byte[] generatePayload(byte timeout, byte lockFlag, byte[] content) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(timeout);
        byteArrayOutputStream.write(lockFlag);
        try {
            byteArrayOutputStream.write(content);
        } catch (IOException ignored) {
            //this should be impossible
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte timeout = 0x03;
        byte lockflag = 0x01;
        byte[] content = new byte[15];
        random.nextBytes(content);
        byte[] payload = generatePayload(timeout,lockflag,content);

        WriteNdefCustomMessageCommand command = new WriteNdefCustomMessageCommand();
        command.parsePayload(payload);
        assertEquals(command.getTimeout(), timeout);
        assertEquals(command.getLockflag(), lockflag);
        assertEquals(command.willLock(),(lockflag == 0x01));
        assertArrayEquals(command.getContentBytes(),content);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte timeout = 0x03;
        byte lockflag = 0x01;
        byte[] content = new byte[15];
        random.nextBytes(content);
        byte[] payload = generatePayload(timeout,lockflag,content);

        WriteNdefCustomMessageCommand command =
                new WriteNdefCustomMessageCommand(timeout,lockflag,content);
        assertArrayEquals(payload,command.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        WriteNdefCustomMessageCommand command = new WriteNdefCustomMessageCommand();
        assertEquals(command.getCommandCode(),0x07);
    }
}