package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DetectActiveHCETargetCommandCommandTest {

    @Test
    public void testFromPayload() throws Exception {
        DetectActiveHCETargetCommand command = new DetectActiveHCETargetCommand();
        command.parsePayload(new byte[]{(byte)0x05});
        assertEquals(command.getTimeout(),(byte)0x05);
    }

    @Test
    public void testGetPayload() throws Exception {
        DetectActiveHCETargetCommand command = new DetectActiveHCETargetCommand((byte)5);
        assertArrayEquals(command.getPayload(),new byte[]{0x05});
    }

    @Test
    public void testGetCommandCode() throws Exception {
        DetectActiveHCETargetCommand command = new DetectActiveHCETargetCommand();
        assertEquals(command.getCommandCode(), (byte) 0x05);
    }

    @Test
    public void testGetFamilyId() throws Exception {
        DetectActiveHCETargetCommand command = new DetectActiveHCETargetCommand();
        assertArrayEquals(command.getCommandFamily(), new byte[] {0x00,0x04});
    }
}