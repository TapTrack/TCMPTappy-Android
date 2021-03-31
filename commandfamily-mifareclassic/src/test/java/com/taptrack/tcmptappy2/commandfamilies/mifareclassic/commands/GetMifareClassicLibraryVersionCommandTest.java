package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands;

import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.GetMifareClassicLibraryVersionCommand;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetMifareClassicLibraryVersionCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        GetMifareClassicLibraryVersionCommand command = new GetMifareClassicLibraryVersionCommand();
        assertEquals(command.getCommandCode(),(byte)0xFF);

    }
}