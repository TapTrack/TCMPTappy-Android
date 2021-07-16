package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ScanTimeoutResponse;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class EmulateTextRecordCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = (byte)0x09;

    protected byte timeout = 0x00;
    protected byte maxScans = 0x00;

    @NonNull
    protected byte[] text = new byte[0];

    public EmulateTextRecordCommand() {
    }

    public EmulateTextRecordCommand(byte timeout, byte maxScans, @NonNull byte[] textBytes) {
        super();
        this.timeout = timeout;
        this.maxScans = maxScans;
        this.text = textBytes;
    }

    public EmulateTextRecordCommand(byte timeout, byte maxScans, @NonNull String text) {
        this(timeout, maxScans, text.getBytes());
    }

    public EmulateTextRecordCommand(@NonNull byte[] payload) throws MalformedPayloadException {
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
            text = Arrays.copyOfRange(payload, 2, payload.length);
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte[] payload = new byte[2 + text.length];
        payload[0] = timeout;
        payload[1] = maxScans;
        System.arraycopy(text, 0, payload, 2, text.length);
        return payload;
    }

    public byte getTimeout() {
        return timeout;
    }

    /**
     * Set the timeout after which the Tappy will stop scanning and send a
     * {@link ScanTimeoutResponse}
     *
     * 0x00 disables timeout
     * @param timeout
     */
    public void setTimeout(byte timeout) {
        this.timeout = timeout;
    }

    public byte[] getTextBytes() {
        return text;
    }

    public String getText() {
        return new String(text);
    }

    public void setText(byte[] text) {
        this.text = text;
    }

    public void setText(String text) {
        try {
            this.text = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //this should not happen
        }
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
