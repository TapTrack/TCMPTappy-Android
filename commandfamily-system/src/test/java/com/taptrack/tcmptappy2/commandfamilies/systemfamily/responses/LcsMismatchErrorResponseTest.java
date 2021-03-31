package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LcsMismatchErrorResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        LcsMismatchErrorResponse response = new LcsMismatchErrorResponse();
        assertEquals(response.getCommandCode(),0x02);
    }
}