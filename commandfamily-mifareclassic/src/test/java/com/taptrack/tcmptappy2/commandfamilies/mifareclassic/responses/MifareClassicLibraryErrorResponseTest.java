package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses.MifareClassicLibraryErrorResponse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MifareClassicLibraryErrorResponseTest {

    @Test
    public void testGetCommandCode() throws Exception {
        MifareClassicLibraryErrorResponse response = new MifareClassicLibraryErrorResponse();
        assertEquals(response.getCommandCode(),0x7F);
    }
}