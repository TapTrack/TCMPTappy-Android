package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;

public class EmulationSuccessResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x07;

    public EmulationSuccessResponse() {
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
