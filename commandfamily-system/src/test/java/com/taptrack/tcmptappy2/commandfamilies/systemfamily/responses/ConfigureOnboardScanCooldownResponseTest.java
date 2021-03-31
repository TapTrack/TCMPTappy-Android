package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigureOnboardScanCooldownResponseTest {

    private byte[] generateTestPayload(byte configItem, byte[] duration) {
        ByteArrayOutputStream boStream = new ByteArrayOutputStream(3);
        boStream.write(configItem);
        try {
            boStream.write(duration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return boStream.toByteArray();
    }

    @Test
    public void testParsePayload() throws Exception {
        byte cooldownSetting = 0x01;
        int durationSetting = 5;
        byte[] testPayload = generateTestPayload(cooldownSetting,new byte[]{0x00,0x05});

        ConfigureOnboardScanCooldownResponse response = new ConfigureOnboardScanCooldownResponse();
        response.parsePayload(testPayload);

        assertTrue(response.isCooldownEnabled());
        assertEquals(response.getBufferSize(),durationSetting);
    }

    @Test
    public void testGetPayload() throws Exception {
        boolean item = false;
        int value = 7;
        byte[] testPayload = generateTestPayload((byte)0x00,new byte[]{0x00,0x07});

        ConfigureOnboardScanCooldownResponse response = new ConfigureOnboardScanCooldownResponse(item,value);

        assertArrayEquals(testPayload,response.getPayload());
    }



    @Test
    public void testGetResponseCode() throws Exception {
        ConfigureOnboardScanCooldownResponse response = new ConfigureOnboardScanCooldownResponse();
        assertEquals(response.getCommandCode(),0x0D);
    }
}