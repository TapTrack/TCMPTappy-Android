package com.taptrack.tcmptappy2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

class TCMPUtils {
    static byte[] calculateCRCBitwise(byte[] data) {
        int crc = 0x6363;
        for(int i = 0; i < data.length; ++i) {
            crc = update_cr16(crc, data[i]);
        }

        return shortToByteArray((short)crc);
    }

    private static int update_cr16(int crc, byte b) {
        int i, v, tcrc = 0;

        v = (int) ((crc ^ b) & 0xff);
        for(i = 0; i < 8; i++) {
            tcrc = (int) ((( (tcrc ^ v) & 1) != 0) ? (tcrc >> 1) ^ 0x8408 : tcrc >>1);
            v >>= 1;
        }

        return (int) (((crc >> 8) ^ tcrc) & 0xffff);
    }

    private static byte[] shortToByteArray(final short value) {
        return new byte[] { (byte) (value >>> 8), (byte) (value) };
    }

    static byte[] concatByteArr(byte[]... byteArrays) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for(byte[] byteArray : byteArrays) {
            stream.write(byteArray);
        }
        return stream.toByteArray();
    }

    public static boolean validate(byte[] data) throws IllegalArgumentException, MessageGarbledException {
        if(data.length >= 8) {
            byte l1 = data[0];
            byte l2 = data[1];
            byte lcs = data[2];
            byte[] family = new byte[]{data[3],data[4]};
            byte command = data[5];
            byte[] crc = new byte[] {data[data.length - 2], data[data.length-1]};

            byte[] toCheckCRC = Arrays.copyOfRange(data,0,data.length-2);
            byte[] toCheckLength = Arrays.copyOfRange(data,3,data.length);
            byte[] calculatedCRC = calculateCRCBitwise(toCheckCRC);

            byte calculatedLcs = (byte) ((((byte) 0xFF - (((l1 & 0xff) + (l2 & 0xff)) & 0xff)) + (0x01 & 0xff)) & 0xff);

            if(calculatedLcs == lcs && Arrays.equals(crc,calculatedCRC)) {
                int expectedLength = ((l1 & 0xff) << 8) + (l2 & 0xff);
                if(expectedLength == toCheckLength.length) {
                    return true;
                }
                else {
                    throw new IllegalArgumentException("Body of command too short");
                }
            }
            else {
                throw new MessageGarbledException("Bad CRC or LCS");
            }
        }
        else {
            throw new IllegalArgumentException("Command too short");
        }
    }
}
