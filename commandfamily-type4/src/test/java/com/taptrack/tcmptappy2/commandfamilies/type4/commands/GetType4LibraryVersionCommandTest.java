package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;

import static org.junit.Assert.*;

public class GetType4LibraryVersionCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        GetType4LibraryVersionCommand command = new GetType4LibraryVersionCommand();
        assertEquals(command.getCommandCode(),(byte)0xFF);
    }

}