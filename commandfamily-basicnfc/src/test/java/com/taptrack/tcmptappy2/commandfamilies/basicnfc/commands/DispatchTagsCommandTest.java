package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.DispatchTagsCommand;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DispatchTagsCommandTest {

    @Test
    public void testGetCommandCode() throws Exception {
        DispatchTagsCommand command = new DispatchTagsCommand();
        assertEquals(command.getCommandCode(), 0x0C);
    }

    @Test
    public void testParsePayload() throws Exception {
        byte timeout = 0x01;
        byte[] payload = new byte[]{timeout};

        DispatchTagsCommand command = new DispatchTagsCommand();
        command.parsePayload(payload);
        assertEquals(command.getTimeout(), timeout);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte timeout = 0x01;

        byte[] payload = new byte[]{timeout};
        DispatchTagsCommand command = new DispatchTagsCommand(timeout);
        assertArrayEquals(command.getPayload(), payload);
    }
}