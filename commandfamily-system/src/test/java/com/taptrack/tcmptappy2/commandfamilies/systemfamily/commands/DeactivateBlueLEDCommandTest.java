package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeactivateBlueLEDCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        DeactivateBlueLEDCommand pingCommand = new DeactivateBlueLEDCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x11);
    }
}