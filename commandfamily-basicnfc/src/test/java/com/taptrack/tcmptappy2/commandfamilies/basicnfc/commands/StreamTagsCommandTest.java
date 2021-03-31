package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StreamTagsCommand;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StreamTagsCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        StreamTagsCommand command = new StreamTagsCommand();
        assertEquals(command.getCommandCode(), 0x01);
    }
}