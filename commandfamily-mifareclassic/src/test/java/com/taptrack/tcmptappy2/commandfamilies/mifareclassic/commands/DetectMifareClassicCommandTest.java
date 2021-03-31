package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands;

import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DetectMifareClassicCommandTest {

    private byte[] generateTestPayload(byte timeout) {
        return new byte[]{timeout};
    }

    @Test
    public void testParsePayload() throws Exception {
        byte timeout = 0x04;
        byte[] testPayload = generateTestPayload(timeout);

        DetectMifareClassicCommand command = new DetectMifareClassicCommand();
        command.parsePayload(testPayload);

        assertEquals(command.getTimeout(),timeout);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte timeout = 0x04;
        byte[] testPayload = generateTestPayload(timeout);

        DetectMifareClassicCommand command = new DetectMifareClassicCommand(timeout);

        assertArrayEquals(command.getPayload(), testPayload);
    }

    @Test
    public void testGetCommandCode() throws Exception {
        DetectMifareClassicCommand command = new DetectMifareClassicCommand();
        assertEquals(command.getCommandCode(),0x02);
    }
}