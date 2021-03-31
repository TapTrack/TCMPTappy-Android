package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * Command for requesting that the Tappy respond with the version of the
 * Type 4 library supported on this device.
 */
public class GetType4LibraryVersionCommand extends AbstractType4Message {
    public static final byte COMMAND_CODE = (byte) 0xFF;

    public GetType4LibraryVersionCommand() {
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
