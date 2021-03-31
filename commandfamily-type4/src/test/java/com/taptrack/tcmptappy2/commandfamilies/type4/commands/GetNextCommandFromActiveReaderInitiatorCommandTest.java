package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetNextCommandFromActiveReaderInitiatorCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        GetNextCommandFromActiveReaderInitiatorCommand command = new GetNextCommandFromActiveReaderInitiatorCommand();
        assertEquals(command.getCommandCode(),(byte)0x07);
    }

}