package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeactivateBuzzerCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        DeactivateBuzzerCommand pingCommand = new DeactivateBuzzerCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x14);
    }
}