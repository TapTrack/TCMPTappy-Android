package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeactivateGreenLEDCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        DeactivateGreenLEDCommand pingCommand = new DeactivateGreenLEDCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x0E);
    }
}