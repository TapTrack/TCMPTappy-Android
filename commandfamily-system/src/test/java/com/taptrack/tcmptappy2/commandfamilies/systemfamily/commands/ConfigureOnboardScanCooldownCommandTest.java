package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ConfigureOnboardScanCooldownCommandTest {

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
        byte cooldownSetting = 0x45;
        int durationSetting = 5;
        byte[] testPayload = generateTestPayload(cooldownSetting,new byte[]{0x00,0x05});

        ConfigureOnboardScanCooldownCommand command = new ConfigureOnboardScanCooldownCommand();
        command.parsePayload(testPayload);

        assertEquals(command.getCooldownSetting(), cooldownSetting);
        assertEquals(command.getBufferSize(),durationSetting);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte item = 0x45;
        int value = 7;
        byte[] testPayload = generateTestPayload(item,new byte[]{0x00,0x07});

        ConfigureOnboardScanCooldownCommand command = new ConfigureOnboardScanCooldownCommand(item,value);

        assertArrayEquals(testPayload,command.getPayload());
    }



    @Test
    public void testGetCommandCode() throws Exception {
        ConfigureOnboardScanCooldownCommand command = new ConfigureOnboardScanCooldownCommand();
        assertEquals(command.getCommandCode(),0x08);
    }
}