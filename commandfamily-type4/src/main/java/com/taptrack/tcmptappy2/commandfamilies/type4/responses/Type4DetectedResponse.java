package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import android.util.Log;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Response for a Type 4 tag being detected. Includes the tag's UID
 * as well as the response to the RATS (Request for Answer to Select) command
 * if the card provided one.
 */
public class Type4DetectedResponse extends AbstractType4Message {
    public static final byte COMMAND_CODE = 0x01;

    private byte[] uid;
    private byte[] ats;

    public Type4DetectedResponse() {
        uid = new byte[0];
        ats = new byte[0];
    }

    public Type4DetectedResponse(byte[] uid, byte[] ats) {
        this.uid = uid;
        this.ats = ats;
    }

    public byte[] getUid() {
        return uid;
    }

    public void setUid(byte[] uid) {
        this.uid = uid;
    }

    public byte[] getAts() {
        return ats;
    }

    public void setAts(byte[] ats) {
        this.ats = ats;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length == 0) {
            throw new MalformedPayloadException("Payload must be at least a single byte");
        }

        int uidLength = payload[0] & 0xff;

        if((uidLength + 1) > payload.length) {
            throw new MalformedPayloadException("Payload too short to contain UID length specified");
        }

        uid = Arrays.copyOfRange(payload,1,uidLength+1);

        if(payload.length > uidLength+1) {
            ats = Arrays.copyOfRange(payload,(uidLength+1),payload.length);
        }
        else {
            ats = new byte[0];
        }

    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1+uid.length+ats.length);
        byteArrayOutputStream.write((byte) uid.length);
        try {
            byteArrayOutputStream.write(uid);
            byteArrayOutputStream.write(ats);
        } catch (IOException e) {
            Log.wtf(Type4DetectedResponse.class.getSimpleName(), "Error composing payload", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
