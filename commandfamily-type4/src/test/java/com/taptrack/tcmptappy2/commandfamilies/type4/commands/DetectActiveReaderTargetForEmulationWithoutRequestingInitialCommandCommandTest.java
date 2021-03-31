package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommandTest {

    @Test
    public void testParsePayload() throws Exception {
        DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand command = new DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand();
        command.parsePayload(new byte[]{(byte)0x05});
        assertEquals(command.getTimeout(),(byte)0x05);
    }

    @Test
    public void testGetPayload() throws Exception {
        DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand command = new DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand((byte)5);
        assertArrayEquals(command.getPayload(),new byte[]{0x05});
    }

    @Test
    public void testGetCommandCode() throws Exception {
        DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand command = new DetectActiveReaderTargetForEmulationWithoutRequestingInitialCommandCommand();
        assertEquals(command.getCommandCode(),0x06);
    }
}