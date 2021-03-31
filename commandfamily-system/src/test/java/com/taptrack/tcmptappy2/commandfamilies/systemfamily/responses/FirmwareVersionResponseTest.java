package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FirmwareVersionResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        FirmwareVersionResponse response = new FirmwareVersionResponse();
        assertEquals(response.getCommandCode(),0x06);
    }
}