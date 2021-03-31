package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * Command for telling the Tappy to search for an NFC Forum Type 4 tag that uses ISO14443B
 * modulation and has a specific AFI
 *
 * In the Android NFC SDK, this corresponds to an IsoDep tag.
 *
 * Note: for all uses of timeout, a value of 0 corresponds to infinite polling.
 */
public class DetectType4BSpecificAfiCommand extends AbstractType4Message {
    public static final byte COMMAND_CODE = (byte) 0x04;
    private byte timeout;
    private byte afi;

    public DetectType4BSpecificAfiCommand() {
        this.timeout = 0;
        this.afi = 0;
    }

    public DetectType4BSpecificAfiCommand(byte timeout, byte afi) {
        this.timeout = timeout;
        this.afi = afi;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length == 2) {
            timeout = payload[0];
            afi = payload[1];
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

    public byte getAfi() {
        return afi;
    }

    public void setAfi(byte afi) {
        this.afi = afi;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return new byte[]{timeout,afi};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
