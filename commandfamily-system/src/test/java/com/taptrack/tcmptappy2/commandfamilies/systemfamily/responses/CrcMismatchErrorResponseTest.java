package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CrcMismatchErrorResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        CrcMismatchErrorResponse response = new CrcMismatchErrorResponse();
        assertEquals(response.getCommandCode(),0x03);
    }
}