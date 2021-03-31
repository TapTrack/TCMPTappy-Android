package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AutoPollCommandTest {
    private byte[] generatePayload(byte typeIndicator, byte hbPeriod, boolean suppressBuzzer) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(typeIndicator);
        byteArrayOutputStream.write(hbPeriod);
        if (suppressBuzzer) {
            byteArrayOutputStream.write(0x01);
        } else {
            byteArrayOutputStream.write(0x00);
        }

        return byteArrayOutputStream.toByteArray();
    }


    @Test
    public void testParsePayload() throws Exception {
        byte type = 0x03;
        byte hbPeriod = 0x01;
        boolean suppressBuzzer = false;
        byte[] payload = generatePayload(type,hbPeriod,suppressBuzzer);

        AutoPollCommand command = new AutoPollCommand();
        command.parsePayload(payload);
        assertEquals(command.getHeartBeatPeriod(), hbPeriod);
        assertEquals(command.getScanModeIndicator(), type);
        assertEquals(command.isBuzzerDisabled(),suppressBuzzer);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte type = 0x04;
        byte hbPeriod = 0x02;
        boolean suppressBuzzer = true;
        byte[] payload = generatePayload(type,hbPeriod,suppressBuzzer);

        AutoPollCommand command = new AutoPollCommand(type,hbPeriod,suppressBuzzer);
        assertArrayEquals(payload,command.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        AutoPollCommand command = new AutoPollCommand();
        assertEquals(command.getCommandCode(),0x10);
    }
}