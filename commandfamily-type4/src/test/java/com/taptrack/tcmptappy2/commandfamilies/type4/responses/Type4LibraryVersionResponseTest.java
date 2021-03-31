package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Type4LibraryVersionResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        Type4LibraryVersionResponse response = new Type4LibraryVersionResponse();
        assertEquals(response.getCommandCode(),(byte) 0x05);
    }

}