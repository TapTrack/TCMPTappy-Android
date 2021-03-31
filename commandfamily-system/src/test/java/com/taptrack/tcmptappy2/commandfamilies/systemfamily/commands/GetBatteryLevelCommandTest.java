package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetBatteryLevelCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        GetBatteryLevelCommand command = new GetBatteryLevelCommand();
        assertEquals(command.getCommandCode(),0x02);
    }
}