package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.ScanNdefCommand;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScanNdefCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ScanNdefCommand command = new ScanNdefCommand();
        assertEquals(command.getCommandCode(),0x04);
    }
}