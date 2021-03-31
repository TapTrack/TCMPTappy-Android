package com.taptrack.tcmptappy2.commandfamilies.basicnfc;

public class AutoPollingConstants {
    public static class ScanModes {
        // Scan for type 2 tags (eg NTAG, Mifare Ultralight, etc)
        public static final byte TYPE_2 = 0x00;
        // Scan for type 1 tags (eg Jewel, Topaz, etc)
        public static final byte TYPE_1 = 0X01;
        // Scan for ISO 14443-4B tags
        public static final byte TYPE_4B = 0X02;
        // Scan for Felicia tags (eg type 3)
        public static final byte FELICIA = 0x03;
        // Scan for ISO 14443-4A tags
        public static final byte TYPE_4A = 0x04;
        // Scan for all tags
        public static final byte ALL = 0x05;
    }
    
    public static class ResponseTagTypes {
        // Detected for type 2 tags (eg NTAG, Mifare Ultralight, etc)
        public static final byte TYPE_2 = 0x00;
        // Detected for type 1 tags (eg Jewel, Topaz, etc)
        public static final byte TYPE_1 = 0X01;
        // Detected for ISO 14443-4B tags
        public static final byte TYPE_4B = 0X02;
        // Detected for Felicia (eg type 3)
        public static final byte FELICIA = 0x03;
        // Detected for ISO 14443-4A tags
        public static final byte TYPE_4A = 0x04;
        // Detected for all tags
        public static final byte UNRECOGNIZED_TYPE = 0x05;
    }

    private AutoPollingConstants() {

    }
}
