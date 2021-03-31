package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeactivateRedLEDCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        DeactivateRedLEDCommand pingCommand = new DeactivateRedLEDCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x0B);
    }
}