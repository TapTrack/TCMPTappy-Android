package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import android.util.Log;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Response for a Type 4 tag being detected htat uses ISO14443B modulation.
 * Includes the tag's ATTRIB and ATQB responses
 */
public class Type4BDetectedResponse extends AbstractType4Message {
    public static final byte COMMAND_CODE = 0x07;

    private byte[] atqb;
    private byte[] attrib;

    public Type4BDetectedResponse() {
        atqb = new byte[0];
        attrib = new byte[0];
    }

    public Type4BDetectedResponse(byte[] atqb, byte[] attrib) {
        this.atqb = atqb;
        this.attrib = attrib;
    }

    public byte[] getAtqb() {
        return atqb;
    }

    public void setAtqb(byte[] atqb) {
        this.atqb = atqb;
    }

    public byte[] getAttrib() {
        return attrib;
    }

    public void setAttrib(byte[] attrib) {
        this.attrib = attrib;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 2) {
            throw new MalformedPayloadException("Payload must be at least a two bytes");
        }

        int atqbLength = payload[0] & 0xff;
        int attribLength = payload[1] & 0xff;

        if((atqbLength + attribLength + 2) > payload.length) {
            throw new MalformedPayloadException("Payload too short to contain ATTRIB and ATQB " +
                    "lengths specified");
        }

        if(atqbLength > 0) {
            atqb = Arrays.copyOfRange(payload, 2, atqbLength+2);
        }
        else {
            atqb = new byte[0];
        }

        if(attribLength > 0) {
            attrib = Arrays.copyOfRange(payload,2+atqbLength,2+atqbLength+attribLength);
        }
        else {
            attrib = new byte[0];
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2+attrib.length+atqb.length);
        try {
            byteArrayOutputStream.write((byte)atqb.length);
            byteArrayOutputStream.write((byte)attrib.length);
            byteArrayOutputStream.write(atqb);
            byteArrayOutputStream.write(attrib);
        } catch (IOException e) {
            Log.wtf(Type4BDetectedResponse.class.getSimpleName(), "Error composing payload", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
