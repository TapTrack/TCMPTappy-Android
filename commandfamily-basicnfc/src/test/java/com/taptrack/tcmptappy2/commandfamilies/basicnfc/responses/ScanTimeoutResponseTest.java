package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ScanTimeoutResponse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScanTimeoutResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ScanTimeoutResponse response = new ScanTimeoutResponse();
        assertEquals(response.getCommandCode(),0x03);
    }

}