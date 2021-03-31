package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.GetBasicNfcLibraryVersionCommand;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetBasicNfcLibraryVersionCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        GetBasicNfcLibraryVersionCommand tcmpMessage = new GetBasicNfcLibraryVersionCommand();
        assertEquals(tcmpMessage.getCommandCode(),(byte) 0xFF);
    }
}