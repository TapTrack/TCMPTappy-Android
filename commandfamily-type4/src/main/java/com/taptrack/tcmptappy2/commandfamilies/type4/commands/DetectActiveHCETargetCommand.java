package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * Command for telling the Tappy to search for an active target for emulation mode
 *
 * In the Android NFC SDK, this corresponds to using the reader for host card emulation
 *
 * Note: for all uses of timeout, a value of 0 corresponds to infinite polling.
 */
public class DetectActiveHCETargetCommand extends AbstractType4Message {
    public static final byte COMMAND_CODE = (byte) 0x05;
    private byte timeout;

    public DetectActiveHCETargetCommand() {
        this.timeout = 0;
    }

    public DetectActiveHCETargetCommand(byte timeout) {
        this.timeout = timeout;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length == 1) {
            timeout = payload[0];
        }
        else {
            throw new MalformedPayloadException("Payload should be a single byte");
        }
    }

    public byte getTimeout() {
        return timeout;
    }

    public void setTimeout(byte timeout) {
        this.timeout = timeout;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return new byte[]{timeout};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
