package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ByteFormatConverters {
    public static byte[] unsignedInt16ToByteArray(int value) {
        return new byte[]{(byte)((value & 0xFF00 ) >>> 8),(byte) value};
    }

    public static int bytePairToUnsignedInt16(byte msb, byte lsb) {
        return ((msb & 0x00FF) << 8) | (lsb & 0x00FF);
    }

    public static int fourByteToUnsignedInt32(byte[] bytes){
        return ((bytes[0] & 0x00FF) << 0 |
                (bytes[1] & 0x00FF) << 8 |
                (bytes[2] & 0x00FF) << 16|
                (bytes[3] & 0x00FF) << 24);
    }

    public static int bcdToInt(byte bcd) {
        int tens = ((bcd & 0xF0) >>> 4) & 0xff;
        int ones = ((bcd & 0x0F)) & 0xFF;
        return tens*10+ones;
    }

    public static byte intToBcd(int value) {
        int tens = Math.min(value / 10,9);
        int ones = Math.min(value % 10,9);
        return (byte) (((tens & 0x0F) << 4) | (ones & 0x0F));
    }

    public static byte[] padToLength(byte[] data, int desiredLength, byte padByte) {
        if(data.length > desiredLength)
            throw new IllegalArgumentException("Data is longer than desired length");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(desiredLength);
        try {
            outputStream.write(data);
        } catch (IOException e) {
            throw new IllegalStateException("This should be impossible",e);
        }
        int neededPadding = desiredLength - data.length;
        for(int i = 0; i < neededPadding; i++) {
            outputStream.write(padByte);
        }

        return outputStream.toByteArray();
    }

    public static byte[] stripPadding(byte[] data, byte padByte) {
        int lastIndex = data.length - 1;
        while (lastIndex >= 0 && data[lastIndex] == padByte) {
            lastIndex--;
        }

        return Arrays.copyOfRange(data,0,lastIndex >= 0 ? lastIndex + 1 : 0);
    }
}
