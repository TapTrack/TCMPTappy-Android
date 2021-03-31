package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SystemErrorResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        SystemErrorResponse response = new SystemErrorResponse();
        assertEquals(response.getCommandCode(),0x7F);
    }
}