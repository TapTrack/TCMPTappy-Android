package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * Response indicating that a scan for type 4 tags has timed out.
 */
public class Type4TimeoutResponse extends AbstractType4Message {
    public static final byte COMMAND_CODE = 0x03;


    public Type4TimeoutResponse() {

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
