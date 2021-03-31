package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Type4ErrorResponseTest {
    @Test
    public void testGetCommandCode() throws Exception {
        Type4ErrorResponse response = new Type4ErrorResponse();
        assertEquals(response.getCommandCode(), (byte) 0x7F);
    }

}