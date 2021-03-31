package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;
import static org.junit.Assert.*;

public class DetectType4CommandTest {

    @Test
    public void testFromPayload() throws Exception {
        DetectType4Command command = new DetectType4Command();
        command.parsePayload(new byte[]{(byte)0x05});
        assertEquals(command.getTimeout(),(byte)0x05);
    }

    @Test
    public void testGetPayload() throws Exception {
        DetectType4Command command = new DetectType4Command((byte)5);
        assertArrayEquals(command.getPayload(),new byte[]{0x05});
    }

    @Test
    public void testGetCommandCode() throws Exception {
        DetectType4Command command = new DetectType4Command();
        assertEquals(command.getCommandCode(), (byte) 0x01);
    }

    @Test
    public void testGetFamilyId() throws Exception {
        DetectType4Command command = new DetectType4Command();
        assertArrayEquals(command.getCommandFamily(), new byte[] {0x00,0x04});
    }
}