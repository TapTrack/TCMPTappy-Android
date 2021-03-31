package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

public class Type4PollingErrorResponse extends AbstractType4Message {
    public static final byte COMMAND_CODE = 0x04;


    public Type4PollingErrorResponse() {

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
