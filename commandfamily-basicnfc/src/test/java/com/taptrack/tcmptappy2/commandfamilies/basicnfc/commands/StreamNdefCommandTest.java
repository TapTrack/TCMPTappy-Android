package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StreamNdefCommand;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StreamNdefCommandTest {


    @Test
    public void testGetCommandCode() throws Exception {
        StreamNdefCommand command = new StreamNdefCommand();
        assertEquals(command.getCommandCode(),0x03);
    }
}