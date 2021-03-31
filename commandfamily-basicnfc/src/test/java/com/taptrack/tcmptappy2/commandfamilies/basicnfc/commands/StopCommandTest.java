package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StopCommand;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StopCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        StopCommand command = new StopCommand();
        assertEquals(command.getCommandCode(),0x00);
    }
}