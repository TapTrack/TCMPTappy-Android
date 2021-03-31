package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy.tappy.constants.TagTypes;
import com.taptrack.tcmptappy2.MalformedPayloadException;;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;

import java.util.Arrays;

/**
 * A tag has been written by the Tappy
 */
public class TagWrittenResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x05;
    byte[] tagCode;
    byte tagType;

    public TagWrittenResponse() {
        tagCode = new byte[7];
        tagType = TagTypes.TAG_UNKNOWN;
    }

    public TagWrittenResponse(byte[] tagCode, byte tagType) {
        this.tagCode = tagCode;
        this.tagType = tagType;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        tagType = payload[0];
        tagCode = Arrays.copyOfRange(payload, 1, payload.length);
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
        byte[] payload = new byte[tagCode.length+1];
        payload[0] = tagType;
        System.arraycopy(tagCode,0,payload,1, tagCode.length);
        return payload;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
