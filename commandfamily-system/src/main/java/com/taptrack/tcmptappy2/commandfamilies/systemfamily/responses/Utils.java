package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

/**
 * This is a copy of the Utils file found in Commands because Java lacks a subpackage visibility
 */
abstract class Utils {
    static byte[] intToUint16(int integer) {
        return new byte[]{(byte)(integer >> 8),(byte)(integer)};
    }

    static int uint16ToInt(byte[] bytes) {
        if (bytes.length != 2) {
            throw new IllegalArgumentException("array must contain two bytes");
        } else {
            return ((0xff &bytes[0]) << 8) | (0xff & bytes[1]);
        }
    }
}
