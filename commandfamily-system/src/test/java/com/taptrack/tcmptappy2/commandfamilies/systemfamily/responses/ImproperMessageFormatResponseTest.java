package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImproperMessageFormatResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        ImproperMessageFormatResponse response = new ImproperMessageFormatResponse();
        assertEquals(response.getCommandCode(),0x01);
    }
}