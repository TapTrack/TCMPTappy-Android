package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MifareClassicLibraryVersionResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        MifareClassicLibraryVersionResponse response = new MifareClassicLibraryVersionResponse();
        assertEquals(response.getCommandCode(),0x04);
    }
}