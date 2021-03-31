package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands;

import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.KeySetting;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ReadMifareClassicCommandTest {
    Random rand = new Random();
    private byte[] createTestPayload
            (byte timeout, byte startBlock, byte endblock, byte keySetting, byte[] key) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(timeout);
        byteArrayOutputStream.write(startBlock);
        byteArrayOutputStream.write(endblock);
        byteArrayOutputStream.write(keySetting);
        try {
            byteArrayOutputStream.write(key);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte timeout = 0x01;
        byte startBlock = 0x03;
        byte endBlock = 0x05;
        byte keySetting = KeySetting.KEY_A;
        byte[] key = new byte[6];
        rand.nextBytes(key);

        byte[] testPayload = createTestPayload(timeout,startBlock,endBlock,keySetting,key);
        ReadMifareClassicCommand command = new ReadMifareClassicCommand();
        command.parsePayload(testPayload);

        assertEquals(timeout, command.getTimeout());
        assertEquals(startBlock, command.getStartBlock());
        assertEquals(endBlock,command.getEndBlock());
        assertEquals(keySetting,command.getKeySetting());
        assertArrayEquals(key,command.getKey());
    }

    @Test
    public void testGetPayload() throws Exception {
        byte timeout = 0x01;
        byte startBlock = 0x03;
        byte endBlock = 0x05;
        byte keySetting = KeySetting.KEY_B;
        byte[] key = new byte[6];
        rand.nextBytes(key);

        byte[] testPayload = createTestPayload(timeout,startBlock,endBlock,keySetting,key);

        ReadMifareClassicCommand command = new ReadMifareClassicCommand(timeout,startBlock,endBlock,keySetting,key);

        assertArrayEquals(testPayload,command.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        ReadMifareClassicCommand command = new ReadMifareClassicCommand();
        assertEquals(command.getCommandCode(),0x01);
    }
}