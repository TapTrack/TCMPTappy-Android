package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DisableHostHeartbeatTransmissionResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        DisableHostHeartbeatTransmissionResponse response = new DisableHostHeartbeatTransmissionResponse();
        assertEquals(response.getCommandCode(),0x17);
    }
}