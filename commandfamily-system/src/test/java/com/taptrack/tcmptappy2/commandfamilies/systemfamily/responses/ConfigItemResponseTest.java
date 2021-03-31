package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigItemResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ConfigItemResponse response = new ConfigItemResponse();
        assertEquals(response.getCommandCode(),0x07);
    }
}