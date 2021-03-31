package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivateRedLEDCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ActivateRedLEDCommand pingCommand = new ActivateRedLEDCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x0A);
    }
}