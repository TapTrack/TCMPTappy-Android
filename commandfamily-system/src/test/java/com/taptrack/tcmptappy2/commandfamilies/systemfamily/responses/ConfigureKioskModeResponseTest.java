package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands.ConfigureKioskModeCommand;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigureKioskModeResponseTest {
    @Test
    public void testParsePayloadRequiredOnly() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse();

        byte[] payload = new byte[]{0x01,0x02, (byte) 0x0E,0x02};
        command.parsePayload(payload);

        assertEquals(0x01,command.getPollingSetting());
        assertEquals(0x02,command.getNdefSetting());
        assertEquals(14,command.getHeartbeatPeriod());
        assertEquals(0x02,command.getScanErrorSetting());
    }

    @Test
    public void testGetPayloadRequiredOnly() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse(
                ConfigureKioskModeCommand.PollingSettings.DISABLE_DUAL_POLLING,
                ConfigureKioskModeCommand.NdefSettings.ENABLE_NDEF_DETECTION,
                254,
                ConfigureKioskModeCommand.ScanErrorSettings.ENABLE_SCAN_ERROR_MESSAGES);

        byte[] expectedResults = new byte[]{0x02,0x01, (byte) 0xFE,0x01};

        assertArrayEquals(expectedResults,command.getPayload());
    }

    @Test
    public void testParsePayloadWithSuccess() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse();

        byte[] payload = new byte[]{0x01,0x02, (byte) 0x0E,0x02, (byte) 0xA0, (byte) 0xF4};
        command.parsePayload(payload);

        assertEquals(0x01,command.getPollingSetting());
        assertEquals(0x02,command.getNdefSetting());
        assertEquals(14,command.getHeartbeatPeriod());
        assertEquals(0x02,command.getScanErrorSetting());

        assertTrue(command.didReceiveSuccessfulScanLedTimeout());
        assertFalse(command.didReceiveFailedScanLedTimeout());
        assertFalse(command.didReceivePostScanDelayTimeout());

        assertEquals(41204,command.getSuccessfulScanLedTimeout());
    }


    @Test
    public void testGetPayloadWithSuccess() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse(
                ConfigureKioskModeCommand.PollingSettings.DISABLE_DUAL_POLLING,
                ConfigureKioskModeCommand.NdefSettings.ENABLE_NDEF_DETECTION,
                254,
                ConfigureKioskModeCommand.ScanErrorSettings.ENABLE_SCAN_ERROR_MESSAGES);

        command.setSuccessfulScanLedTimeout(5);
        byte[] expectedResults = new byte[]{0x02,0x01, (byte) 0xFE,0x01,0x00,0x05};

        assertArrayEquals(expectedResults,command.getPayload());
    }

    @Test
    public void testParsePayloadWithSuccessAndFail() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse();

        byte[] payload = new byte[]{0x01,0x02, (byte) 0x0E,0x02, (byte) 0xA0, (byte) 0xF4,0x00,0x07};
        command.parsePayload(payload);

        assertEquals(0x01,command.getPollingSetting());
        assertEquals(0x02,command.getNdefSetting());
        assertEquals(14, command.getHeartbeatPeriod());
        assertEquals(0x02,command.getScanErrorSetting());

        assertTrue(command.didReceiveSuccessfulScanLedTimeout());
        assertTrue(command.didReceiveFailedScanLedTimeout());
        assertFalse(command.didReceivePostScanDelayTimeout());

        assertEquals(41204,command.getSuccessfulScanLedTimeout());
        assertEquals(7,command.getFailedScanLedTimeout());
    }

    @Test
    public void testGetPayloadWithSuccessAndFail() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse(
                ConfigureKioskModeCommand.PollingSettings.DISABLE_DUAL_POLLING,
                ConfigureKioskModeCommand.NdefSettings.ENABLE_NDEF_DETECTION,
                254,
                ConfigureKioskModeCommand.ScanErrorSettings.ENABLE_SCAN_ERROR_MESSAGES);

        command.setSuccessfulScanLedTimeout(5);
        command.setFailedScanLedTimeout(65535);
        byte[] expectedResults = new byte[]{0x02,0x01, (byte) 0xFE,0x01,0x00,0x05, (byte) 0xFF, (byte) 0xFF};

        assertArrayEquals(expectedResults,command.getPayload());
    }

    @Test
    public void testParsePayloadWithSuccessAndFailAndPostScanDelay() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse();

        byte[] payload = new byte[]{0x01,0x02, (byte) 0x0E,0x02, (byte) 0xA0, (byte) 0xF4,0x00,0x07, (byte) 0xF1, (byte) 0xF2};
        command.parsePayload(payload);

        assertEquals(0x01,command.getPollingSetting());
        assertEquals(0x02,command.getNdefSetting());
        assertEquals(14,command.getHeartbeatPeriod());
        assertEquals(0x02,command.getScanErrorSetting());

        assertTrue(command.didReceiveSuccessfulScanLedTimeout());
        assertTrue(command.didReceiveFailedScanLedTimeout());
        assertTrue(command.didReceivePostScanDelayTimeout());

        assertEquals(41204,command.getSuccessfulScanLedTimeout());
        assertEquals(7,command.getFailedScanLedTimeout());
        assertEquals(61938,command.getPostScanDelayTimeout());
    }

    @Test
    public void testGetPayloadWithSuccessAndFailAndPostScanDelay() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse(
                ConfigureKioskModeCommand.PollingSettings.DISABLE_DUAL_POLLING,
                ConfigureKioskModeCommand.NdefSettings.ENABLE_NDEF_DETECTION,
                254,
                ConfigureKioskModeCommand.ScanErrorSettings.ENABLE_SCAN_ERROR_MESSAGES);

        command.setSuccessfulScanLedTimeout(5);
        command.setFailedScanLedTimeout(65535);
        command.setPostScanDelayTimeout(255);

        byte[] expectedResults = new byte[]{0x02,0x01, (byte) 0xFE,0x01,0x00,0x05, (byte) 0xFF, (byte) 0xFF, 0x00, (byte) 0xFF};

        assertArrayEquals(expectedResults,command.getPayload());
    }

    @Test
    public void testGetCommandCode() throws Exception {
        ConfigureKioskModeResponse command = new ConfigureKioskModeResponse();
        assertEquals(command.getCommandCode(),0x0C);
    }
}
