package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivateBlueLEDCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ActivateBlueLEDCommand pingCommand = new ActivateBlueLEDCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x10);
    }
}