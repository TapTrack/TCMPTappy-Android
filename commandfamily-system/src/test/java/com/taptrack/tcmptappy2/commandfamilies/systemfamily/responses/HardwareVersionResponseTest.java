package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HardwareVersionResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        HardwareVersionResponse response = new HardwareVersionResponse();
        assertEquals(response.getCommandCode(), 0x05);
    }
}