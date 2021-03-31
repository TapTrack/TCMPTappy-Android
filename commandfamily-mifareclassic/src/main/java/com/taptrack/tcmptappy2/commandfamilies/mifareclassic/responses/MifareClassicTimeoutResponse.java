package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.AbstractMifareClassicMessage;

/**
 * A mifare classic operation has timed out
 */
public class MifareClassicTimeoutResponse extends AbstractMifareClassicMessage {
    public static final byte COMMAND_CODE = 0x03;

    public MifareClassicTimeoutResponse() {

    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {

    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return new byte[0];
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
