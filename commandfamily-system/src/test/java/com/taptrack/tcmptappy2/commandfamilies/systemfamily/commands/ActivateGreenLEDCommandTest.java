package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivateGreenLEDCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ActivateGreenLEDCommand pingCommand = new ActivateGreenLEDCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x0D);
    }
}