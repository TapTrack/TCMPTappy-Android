package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DetectType4BSpecificAfiCommandTest {

    @Test
    public void testParsePayload() throws Exception {
        DetectType4BSpecificAfiCommand command = new DetectType4BSpecificAfiCommand();
        command.parsePayload(new byte[]{(byte)0x05,(byte)0x23});
        assertEquals(command.getTimeout(),(byte)0x05);
        assertEquals(command.getAfi(),(byte)0x23);
    }

    @Test
    public void testGetPayload() throws Exception {
        DetectType4BSpecificAfiCommand command = new DetectType4BSpecificAfiCommand((byte)5,(byte)0xE1);
        assertArrayEquals(command.getPayload(),new byte[]{0x05,(byte)0xE1});
    }

    @Test
    public void testGetCommandCode() throws Exception {
        DetectType4BSpecificAfiCommand command = new DetectType4BSpecificAfiCommand();
        assertEquals(command.getCommandCode(),0x04);
    }
}