package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class GetBatteryLevelResponseTest {
    private byte[] generateTestPayload(byte batteryLevel) {
        return new byte[]{batteryLevel};
    }

    @Test
    public void testParsePayload() throws Exception {
        byte testLevel = 0x05;
        byte[] payload = generateTestPayload(testLevel);

        GetBatteryLevelResponse response = new GetBatteryLevelResponse();
        response.parsePayload(payload);

        assertEquals(response.getBatteryLevel(),testLevel);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte testLevel = 0x05;
        byte[] payload = generateTestPayload(testLevel);

        GetBatteryLevelResponse response = new GetBatteryLevelResponse();
        response.setBatteryLevel(testLevel);

        assertArrayEquals(payload,response.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        GetBatteryLevelResponse response = new GetBatteryLevelResponse();
        assertEquals(response.getCommandCode(),0x08);
    }
}