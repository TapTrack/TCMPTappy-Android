package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicNfcLibraryVersionResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        BasicNfcLibraryVersionResponse response = new BasicNfcLibraryVersionResponse();
        assertEquals(response.getCommandCode(),0x04);
    }
}