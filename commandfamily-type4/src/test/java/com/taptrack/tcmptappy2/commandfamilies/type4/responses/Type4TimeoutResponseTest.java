package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Type4TimeoutResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        Type4TimeoutResponse timeoutResponse = new Type4TimeoutResponse();
        assertEquals(timeoutResponse.getCommandCode(), 0x03);
    }
}