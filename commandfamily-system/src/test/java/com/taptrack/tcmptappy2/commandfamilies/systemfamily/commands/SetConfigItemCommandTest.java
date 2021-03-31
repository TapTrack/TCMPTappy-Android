package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SetConfigItemCommandTest {

    private byte[] generateTestPayload(byte configItem, byte[] value) {
        ByteArrayOutputStream boStream = new ByteArrayOutputStream(1+value.length);
        boStream.write(configItem);
        try {
            boStream.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return boStream.toByteArray();
    }

    @Test
    public void testParseSingleBytePayload() throws Exception {
        byte item = 0x45;
        byte value = 0x72;
        byte[] testPayload = generateTestPayload(item,new byte[]{value});

        SetConfigItemCommand command = new SetConfigItemCommand();
        command.parsePayload(testPayload);

        assertEquals(command.getParameter(), item);
        assertEquals(command.getValue(),value);
    }

    @Test
    public void testGetSingleBytePayload() throws Exception {
        byte item = 0x45;
        byte value = 0x72;
        byte[] testPayload = generateTestPayload(item,new byte[]{value});

        SetConfigItemCommand command = new SetConfigItemCommand(item,value);

        assertArrayEquals(testPayload,command.getPayload());
    }


    @Test
    public void testParseMultiBytePayload() throws Exception {
        byte item = 0x45;
        byte[] value = new byte[]{0x55,0x72};
        byte[] testPayload = generateTestPayload(item,value);

        SetConfigItemCommand command = new SetConfigItemCommand();
        command.parsePayload(testPayload);

        assertEquals(command.getParameter(), item);
        assertArrayEquals(command.getMultibyteValue(),value);
    }

    @Test
    public void testGetMultiBytePayload() throws Exception {
        byte item = 0x45;
        byte[] value = new byte[]{0x45,(byte)0x82};
        byte[] testPayload = generateTestPayload(item,value);

        SetConfigItemCommand command = new SetConfigItemCommand(item,value);

        assertArrayEquals(testPayload,command.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        SetConfigItemCommand command = new SetConfigItemCommand();
        assertEquals(command.getCommandCode(),0x01);
    }
}