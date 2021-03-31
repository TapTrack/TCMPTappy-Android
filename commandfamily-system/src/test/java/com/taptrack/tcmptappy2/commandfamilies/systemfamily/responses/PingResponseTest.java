package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PingResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        PingResponse response = new PingResponse();
        assertEquals(response.getCommandCode(),(byte)0xFD);
    }
}