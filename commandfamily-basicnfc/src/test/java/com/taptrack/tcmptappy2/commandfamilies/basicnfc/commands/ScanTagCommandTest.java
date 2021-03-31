package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.ScanTagCommand;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScanTagCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ScanTagCommand command = new ScanTagCommand();
        assertEquals(command.getCommandCode(),0x02);
    }
}