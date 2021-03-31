package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PingCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        PingCommand pingCommand = new PingCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0xFD);
    }
}