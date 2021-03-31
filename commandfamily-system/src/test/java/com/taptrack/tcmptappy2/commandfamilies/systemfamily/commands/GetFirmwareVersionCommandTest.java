package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetFirmwareVersionCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        GetFirmwareVersionCommand command = new GetFirmwareVersionCommand();
        assertEquals(command.getCommandCode(),(byte)0xFF);
    }
}