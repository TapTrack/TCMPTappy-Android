package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DetectType4BCommandTest {

    @Test
    public void testParsePayload() throws Exception {
        DetectType4BCommand command = new DetectType4BCommand();
        command.parsePayload(new byte[]{(byte)0x05});
        assertEquals(command.getTimeout(),(byte)0x05);
    }

    @Test
    public void testGetPayload() throws Exception {
        DetectType4BCommand command = new DetectType4BCommand((byte)5);
        assertArrayEquals(command.getPayload(),new byte[]{0x05});
    }

    @Test
    public void testGetCommandCode() throws Exception {
        DetectType4BCommand command = new DetectType4BCommand();
        assertEquals(command.getCommandCode(),0x03);
    }
}