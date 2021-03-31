package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LengthMismatchErrorResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        LengthMismatchErrorResponse response = new LengthMismatchErrorResponse();
        assertEquals(response.getCommandCode(),0x04);
    }
}