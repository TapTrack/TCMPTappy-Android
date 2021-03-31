package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;

import java.net.URI;
import java.util.Arrays;

public class EmulateUriRecordCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = (byte)0x0A;

    private byte timeout = 0x00;
    private byte maxScans = 0x00;
    private byte uriPrefixCode = 0x00;

    @NonNull
    private byte[] uri = new byte[0];

    public EmulateUriRecordCommand() {
    }

    public EmulateUriRecordCommand(
            byte timeout,
            byte maxScans,
            byte uriPrefixCode,
            @NonNull byte[] uriBytes
            ) {
        super();
        this.timeout = timeout;
        this.maxScans = maxScans;
        this.uriPrefixCode = uriPrefixCode;
        this.uri = uriBytes;
    }

    public EmulateUriRecordCommand(
            byte timeout,
            byte maxScans,
            byte uriPrefixCode,
            @NonNull String uri
            ) {
        this(timeout, maxScans, uriPrefixCode, uri.getBytes());
    }

    public EmulateUriRecordCommand(@NonNull byte[] payload) throws MalformedPayloadException {
        super();
        parsePayload(payload);
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if (payload.length < 3) {
            throw new MalformedPayloadException("Payload too short");
        }

        timeout = payload[0];
        maxScans = payload[1];
        uriPrefixCode = payload[2];

        if (payload.length > 3) {
            uri = Arrays.copyOfRange(payload, 3, payload.length);
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte[] payload = new byte[3 + uri.length];
        payload[0] = timeout;
        payload[1] = maxScans;
        payload[2] = uriPrefixCode;
        System.arraycopy(uri, 0, payload, 3, uri.length);
        return payload;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
