package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.LockTagCommand;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LockTagCommandTest {
    protected Random random = new Random();

    protected static byte[] generatePayload(byte timeout, byte[] tagCode) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(timeout);
        outputStream.write(tagCode.length);
        try {
            outputStream.write(tagCode);
        } catch (IOException ignored) {
        }
        return outputStream.toByteArray();
    }

    @Test
    public void testTagRestrictedPayload() throws Exception {
        byte[] tagCode = new byte[7];
        random.nextBytes(tagCode);
        byte timeout = 5;

        byte[] payload = generatePayload(timeout,tagCode);
        LockTagCommand command = new LockTagCommand();
        command.parsePayload(payload);
        assertEquals(timeout, command.getTimeout());
        assertArrayEquals(tagCode, command.getTagCode());
    }

    @Test
    public void testTagNotRestrictedPayload() throws Exception {
        byte timeout = 5;
        byte[] payload = generatePayload(timeout,new byte[0]);
        LockTagCommand command = new LockTagCommand();
        command.parsePayload(payload);
        assertEquals(timeout, command.getTimeout());
        assertEquals(0, command.getTagCode().length);
    }

    @Test
    public void testGetRestrictedPayload() throws Exception {
        byte[] tagCode = new byte[7];
        random.nextBytes(tagCode);
        byte[] payload = generatePayload((byte)7,tagCode);
        LockTagCommand command = new LockTagCommand((byte)7,tagCode);

        assertArrayEquals(payload,command.getPayload());
    }

    @Test
    public void testGetNotRestrictedPayload() throws Exception {
        byte[] payload = generatePayload((byte)7,new byte[0]);
        LockTagCommand command = new LockTagCommand((byte)7,new byte[0]);

        assertArrayEquals(payload,command.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        LockTagCommand command = new LockTagCommand();
        assertEquals(command.getCommandCode(),0x08);
    }
}