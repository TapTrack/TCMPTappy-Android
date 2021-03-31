package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;

import java.util.Arrays;

public class EmulateCustomNdefRecordCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x0B;

    private byte timeout = 0x00;
    private byte maxScans = 0x00;

    @NonNull
    private byte[] content = new byte[0];

    public EmulateCustomNdefRecordCommand() {
    }

    public EmulateCustomNdefRecordCommand(byte timeout, byte maxScans, @NonNull byte[] content) {
        super();
        this.timeout = timeout;
        this.maxScans = maxScans;
        this.content = content;
    }

    public EmulateCustomNdefRecordCommand(@NonNull byte[] payload) throws MalformedPayloadException {
        super();
        parsePayload(payload);
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if (payload.length < 2) {
            throw new MalformedPayloadException("Payload too short");
        }

        timeout = payload[0];
        maxScans = payload[1];

        if (payload.length > 2) {
            content = Arrays.copyOfRange(payload, 2, payload.length);
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte[] payload = new byte[2 + content.length];
        payload[0] = timeout;
        payload[1] = maxScans;
        System.arraycopy(content, 0, payload, 2, content.length);
        return payload;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
