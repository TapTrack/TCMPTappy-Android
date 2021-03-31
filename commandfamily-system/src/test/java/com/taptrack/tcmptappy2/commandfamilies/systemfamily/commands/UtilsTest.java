package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UtilsTest {
    @Test
    public void intToUint16() throws Exception {
        assertArrayEquals("maximum value",new byte[]{(byte) 0xFF, (byte) 0xFF},Utils.intToUint16(65535));
        assertArrayEquals("minimum value",new byte[]{(byte) 0x00, (byte) 0x00},Utils.intToUint16(0));
        assertArrayEquals("maximum single-byte value",new byte[]{(byte) 0x00, (byte) 0xFF},Utils.intToUint16(255));
        assertArrayEquals("maximum single-byte value plus one",new byte[]{(byte) 0x01, (byte) 0x00},Utils.intToUint16(256));
    }

    @Test
    public void uint16ToInt() throws Exception {
        assertEquals("maximum value",65535,Utils.uint16ToInt(new byte[]{(byte) 0xFF, (byte) 0xFF}));
        assertEquals("minimum value",0,Utils.uint16ToInt(new byte[]{(byte) 0x00, (byte) 0x00}));
        assertEquals("maximum single-byte value",255,Utils.uint16ToInt(new byte[]{(byte) 0x00, (byte) 0xFF}));
        assertEquals("maximum single-byte value plus one",256,Utils.uint16ToInt(new byte[]{(byte) 0x01, (byte) 0x00}));
    }

}