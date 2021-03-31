package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class APDUTransceiveSuccessfulResponseTest {

    @Test
    public void testFromPayload() throws Exception {
        byte[] apdu = new byte[]{(byte) 0x00,(byte) 0x03,(byte) 0x53,(byte) 0xFE, (byte) 0xA5};
        APDUTransceiveSuccessfulResponse transceiveApduCommand = new APDUTransceiveSuccessfulResponse();
        transceiveApduCommand.parsePayload(apdu);
        assertArrayEquals(transceiveApduCommand.getPayload(),apdu);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte[] apdu = new byte[]{(byte) 0x00,(byte) 0x03,(byte) 0x53,(byte) 0xFE, (byte) 0xA5};
        APDUTransceiveSuccessfulResponse transceiveApduCommand = new APDUTransceiveSuccessfulResponse(apdu);
        assertArrayEquals(transceiveApduCommand.getPayload(),apdu);
    }

    @Test
    public void testGetCommandCode() throws Exception {
        APDUTransceiveSuccessfulResponse transceiveApduCommand = new APDUTransceiveSuccessfulResponse();
        assertEquals(transceiveApduCommand.getCommandCode(),(byte)0x02);
    }

    @Test
    public void testGetCommandFamily() throws Exception {
        APDUTransceiveSuccessfulResponse transceiveApduCommand = new APDUTransceiveSuccessfulResponse();
        assertArrayEquals(transceiveApduCommand.getCommandFamily(),new byte[]{0x00,0x04});
    }
}