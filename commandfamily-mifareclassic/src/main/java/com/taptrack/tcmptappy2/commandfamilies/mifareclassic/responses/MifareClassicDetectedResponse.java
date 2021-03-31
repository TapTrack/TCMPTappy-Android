package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.AbstractMifareClassicMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Mifare Classic tag was detected by the Tappy
 */
public class MifareClassicDetectedResponse extends AbstractMifareClassicMessage {
    public static final byte COMMAND_CODE = 0x02;
    protected byte type;
    protected byte[] uid;

    public interface ClassicType {
        byte CLASSIC_1K = 0x04;
        byte CLASSIC_4K = 0x05;
    }

    public MifareClassicDetectedResponse() {
        type = 0x00;
        uid = new byte[4];
    }

    /**
     * Mifare Classic was detected
     * @param type one of {@link MifareClassicDetectedResponse.ClassicType}
     * @param uid 4- or 7-byte uid
     * @throws IllegalArgumentException UID is too short to be valid
     */
    public MifareClassicDetectedResponse(byte type, byte[] uid) {
        if(uid.length < 4)
            throw new IllegalArgumentException("UID too short to be valid");

        this.type = type;
        this.uid = uid;
    }

    /**
     * Retreive the type of the Classic read
     *
     * See {@link MifareClassicDetectedResponse.ClassicType}
     * @return classic type
     */
    public byte getType() {
        return type;
    }

    /**
     * Retreive the type of the Classic read
     *
     * See {@link MifareClassicDetectedResponse.ClassicType}
     * @param type the classic type
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * Retrieve the UID of the tag that was detected
     * @return 4- or 7-byte tag UID
     */
    public byte[] getUid() {
        return uid;
    }

    /**
     * Set the UID of the tag that was detected
     * @param uid 4- or 7-byte UID
     * @throws IllegalArgumentException If the UID is less than 4 bytes
     */
    public void setUid(byte[] uid) {
        if(uid.length < 4)
            throw new IllegalArgumentException("UID is too short to be valid");
        this.uid = uid;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 5)
            throw new MalformedPayloadException("Payload too short to be valid");
        type = payload[0];
        uid = Arrays.copyOfRange(payload,1,payload.length);
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(type);
        try {
            outputStream.write(uid);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }

}
