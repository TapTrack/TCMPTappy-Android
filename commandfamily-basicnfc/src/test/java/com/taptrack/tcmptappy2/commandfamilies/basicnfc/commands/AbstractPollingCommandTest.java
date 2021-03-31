package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.PollingModes;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.AbstractPollingCommand;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands.StreamNdefCommand;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AbstractPollingCommandTest {
    private static class TestPollingCommand extends AbstractPollingCommand {

        @Override
        public byte getCommandCode() {
            return 0;
        }
    }

    private byte[] generatePayload(byte timeout, byte mode) {
        return new byte[]{timeout,mode};
    }

    @Test
    public void testParsePayload() throws Exception {
        byte timeout = 0x01;
        byte mode = PollingModes.MODE_GENERAL;
        byte[] payload = generatePayload(timeout,mode);

        TestPollingCommand command = new TestPollingCommand();
        command.parsePayload(payload);
        assertEquals(command.getTimeout(), timeout);
        assertEquals(command.getPollingMode(), mode);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte timeout = 0x01;
        byte mode = PollingModes.MODE_GENERAL;

        byte[] payload = generatePayload(timeout,mode);
        StreamNdefCommand command = new StreamNdefCommand(timeout,mode);
        assertArrayEquals(command.getPayload(), payload);
    }
}