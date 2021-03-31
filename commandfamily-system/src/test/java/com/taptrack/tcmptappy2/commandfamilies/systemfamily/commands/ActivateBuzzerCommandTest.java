package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivateBuzzerCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ActivateBuzzerCommand pingCommand = new ActivateBuzzerCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x13);
    }
}