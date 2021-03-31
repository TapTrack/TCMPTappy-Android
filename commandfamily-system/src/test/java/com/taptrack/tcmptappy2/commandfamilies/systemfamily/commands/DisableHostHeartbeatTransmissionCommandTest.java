package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DisableHostHeartbeatTransmissionCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        DisableHostHeartbeatTransmissionCommand pingCommand = new DisableHostHeartbeatTransmissionCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x07);
    }
}