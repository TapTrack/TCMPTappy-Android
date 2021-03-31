package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BlueLEDDeactivatedResponseTest {
    private byte[] generateTestPayload(
            boolean redLEDActive,
            boolean greenLEDActive,
            boolean blueLEDActive,
            boolean buzzerActive
    ) {
        return new byte[]{
                (byte)(redLEDActive ? 0x01 : 0x00),
                (byte)(greenLEDActive ? 0x01 : 0x00),
                (byte)(blueLEDActive ? 0x01 : 0x00),
                (byte)(buzzerActive ? 0x01 : 0x00)
        };
    }

    @Test
    public void testParsePayload() throws Exception {
        boolean redLEDActive = true;
        boolean greenLEDActive = false;
        boolean blueLEDActive = true;
        boolean buzzerActive = false;
        byte[] payload = generateTestPayload(
                redLEDActive,
                greenLEDActive,
                blueLEDActive,
                buzzerActive
        );

        BlueLEDDeactivatedResponse response = new BlueLEDDeactivatedResponse();
        response.parsePayload(payload);

        assertEquals(response.isRedLEDActive(),redLEDActive);
        assertEquals(response.isBlueLEDActive(),blueLEDActive);
        assertEquals(response.isGreenLEDActive(),greenLEDActive);
        assertEquals(response.isBuzzerActive(),buzzerActive);
    }

    @Test
    public void testGetPayload() throws Exception {
        boolean redLEDActive = false;
        boolean greenLEDActive = true;
        boolean blueLEDActive = false;
        boolean buzzerActive = true;
        byte[] payload = generateTestPayload(
                redLEDActive,
                greenLEDActive,
                blueLEDActive,
                buzzerActive
        );

        BlueLEDDeactivatedResponse response = new BlueLEDDeactivatedResponse();
        response.setRedLEDActive(redLEDActive);
        response.setGreenLEDActive(greenLEDActive);
        response.setBlueLEDActive(blueLEDActive);
        response.setBuzzerActive(buzzerActive);

        assertArrayEquals(payload,response.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        BlueLEDDeactivatedResponse response = new BlueLEDDeactivatedResponse();
        assertEquals(response.getCommandCode(),0x13);
    }
}