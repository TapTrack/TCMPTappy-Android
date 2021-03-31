package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands;


import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.AbstractMifareClassicMessage;

/**
 * Command for requesting that the Tappy respond with the version of the
 * Type 4 library supported on this device.
 */
public class GetMifareClassicLibraryVersionCommand extends AbstractMifareClassicMessage {
    public static final byte COMMAND_CODE = (byte) 0xFF;

    public GetMifareClassicLibraryVersionCommand() {
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
