package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.BasicNfcErrorResponse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicNfcErrorResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        BasicNfcErrorResponse response = new BasicNfcErrorResponse();
        assertEquals(response.getCommandCode(),0x7F);
    }
}