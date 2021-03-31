package com.taptrack.tcmptappy2.commandfamilies.basicnfc;

public class PollingModes {
    /**
     * Polls for Type 1 (Jewel/Topaz) only
     */
    public static final byte MODE_TYPE1 = 0x01;
    /**
     * Polls for Type 2, 4, MIFARE Classic only
     */
    public static final byte MODE_GENERAL = 0x02;
    /**
     * Polls for Type 1, 2, 4, and MIFARE Classic.
     * This polling mode combines {@code MODE_TYPE1} and {@code MODE_GENERAL}.
     */
    public static final byte MODE_DUAL = 0x03;
    /**
     * Polls for Type B only
     */
    public static final byte MODE_TYPE_B = 0x04;
    /**
     * Polls for Felicity Card (FeliCa) 212 only
     */
    public static final byte MODE_FELICA_212 = 0x05;
    /**
     * Polls for Felicity Card (FeliCa) 424 only
     */
    public static final byte MODE_FELICA_424 = 0x06;

    private PollingModes() {

    }
}
