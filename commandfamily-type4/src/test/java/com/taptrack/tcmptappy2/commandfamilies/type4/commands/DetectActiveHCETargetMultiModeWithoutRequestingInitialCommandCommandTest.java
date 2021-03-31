package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommandTest {

    @Test
    public void testFromPayload() throws Exception {
        DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand command = new DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand();
        command.parsePayload(new byte[]{(byte)0x05});
        assertEquals(command.getTimeout(),(byte)0x05);
    }

    @Test
    public void testGetPayload() throws Exception {
        DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand command = new DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand((byte)5);
        assertArrayEquals(command.getPayload(),new byte[]{0x05});
    }

    @Test
    public void testGetCommandCode() throws Exception {
        DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand command = new DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand();
        assertEquals(command.getCommandCode(), (byte) 0x08);
    }

    @Test
    public void testGetFamilyId() throws Exception {
        DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand command = new DetectActiveHCETargetMultiModeWithoutRequestingInitialCommandCommand();
        assertArrayEquals(command.getCommandFamily(), new byte[] {0x00,0x04});
    }
}