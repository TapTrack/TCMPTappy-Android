package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import org.junit.Test;

import static org.junit.Assert.*;

public class TransceiveApduCommandTest {

    @Test
    public void testFromPayload() throws Exception {
        byte[] apdu = new byte[]{(byte) 0x00,(byte) 0x03,(byte) 0x53,(byte) 0xFE, (byte) 0xA5};
        TransceiveApduCommand transceiveApduCommand = new TransceiveApduCommand();
        transceiveApduCommand.parsePayload(apdu);
        assertArrayEquals(transceiveApduCommand.getPayload(),apdu);
    }

    @Test
    public void testGetPayload() throws Exception {
        byte[] apdu = new byte[]{(byte) 0x00,(byte) 0x03,(byte) 0x53,(byte) 0xFE, (byte) 0xA5};
        TransceiveApduCommand transceiveApduCommand = new TransceiveApduCommand(apdu);
        assertArrayEquals(transceiveApduCommand.getPayload(),apdu);
    }

    @Test
    public void testGetCommandCode() throws Exception {
        TransceiveApduCommand transceiveApduCommand = new TransceiveApduCommand();
        assertEquals(transceiveApduCommand.getCommandCode(),(byte)0x02);
    }

    @Test
    public void testGetCommandFamily() throws Exception {
        TransceiveApduCommand transceiveApduCommand = new TransceiveApduCommand();
        assertArrayEquals(transceiveApduCommand.getCommandFamily(),new byte[]{0x00,0x04});
    }
}