package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ActivateBuzzerForDurationCommandTest {

    @Test
    public void testParsePayload() throws Exception {
        int testLevel = 750;
        byte[] payload = new byte[]{0x02, (byte) 0xEE};

        ActivateBuzzerForDurationCommand response = new ActivateBuzzerForDurationCommand();
        response.parsePayload(payload);

        assertEquals(response.getDuration(),testLevel);
    }

    @Test
    public void testGetPayload() throws Exception {
        int testLevel = 1234;
        byte[] payload = new byte[]{0x04, (byte) 0xD2};

        ActivateBuzzerForDurationCommand response = new ActivateBuzzerForDurationCommand();
        response.setDuration(testLevel);

        assertArrayEquals(payload,response.getPayload());
    }


    @Test
    public void testGetCommandCode() throws Exception {
        ActivateBuzzerForDurationCommand pingCommand = new ActivateBuzzerForDurationCommand();
        assertEquals(pingCommand.getCommandCode(),(byte)0x15);
    }


}