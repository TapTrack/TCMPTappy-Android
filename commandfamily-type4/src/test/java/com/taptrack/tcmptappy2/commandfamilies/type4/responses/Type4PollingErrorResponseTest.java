package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Type4PollingErrorResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        Type4PollingErrorResponse errorResponse = new Type4PollingErrorResponse();
        assertEquals(errorResponse.getCommandCode(),0x04);
    }
}