package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy.tappy.constants.TagTypes;
import com.taptrack.tcmptappy2.MalformedPayloadException;;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;

import java.util.Arrays;

/**
 * A tag has been written by the Tappy
 */
public class TagLockedResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x06;
    byte[] tagCode;
    byte tagType;

    public TagLockedResponse() {
        tagCode = new byte[7];
        tagType = TagTypes.TAG_UNKNOWN;
    }

    public TagLockedResponse(byte[] tagCode, byte tagType) {
        this.tagCode = tagCode;
        this.tagType = tagType;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 2) {
            throw new MalformedPayloadException("Payload too short to contain a tag locked response");
        }

        int tagCodeLength = payload[1];
        if((2+tagCodeLength) < payload.length) {
            throw new MalformedPayloadException("Payload too short to contain tag code of length specified");
        }

        tagType = payload[0];
        tagCode = Arrays.copyOfRange(payload, 2, 2+tagCodeLength);
    }

    public byte[] getTagCode() {
        return tagCode;
    }

    public void setTagCode(byte[] tagCode) {
        this.tagCode = tagCode;
    }

    public byte getTagType() {
        return tagType;
    }

    public void setTagType(byte tagType) {
        this.tagType = tagType;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte[] payload = new byte[tagCode.length+2];
        payload[0] = tagType;
        payload[1] = (byte)tagCode.length;
        System.arraycopy(tagCode, 0, payload, 2, tagCode.length);
        return payload;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
