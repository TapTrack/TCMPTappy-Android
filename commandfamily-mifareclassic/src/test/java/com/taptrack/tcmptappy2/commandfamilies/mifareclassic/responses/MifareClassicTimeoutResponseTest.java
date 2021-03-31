package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MifareClassicTimeoutResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        MifareClassicTimeoutResponse response = new MifareClassicTimeoutResponse();
        assertEquals(response.getCommandCode(),0x03);
    }
}