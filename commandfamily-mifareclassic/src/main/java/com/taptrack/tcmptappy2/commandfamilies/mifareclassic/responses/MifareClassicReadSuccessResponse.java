package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.AbstractMifareClassicMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Response indicating a successful read from a MIFARE Classic
 */
public class MifareClassicReadSuccessResponse extends AbstractMifareClassicMessage {
    public static final byte COMMAND_CODE = 0x01;
    protected byte startBlock;
    protected byte endBlock;
    protected byte[] uid;
    protected byte[] data;

    public MifareClassicReadSuccessResponse() {
        startBlock = 0x00;
        endBlock = 0x00;
        uid = new byte[0];
        data = new byte[0];
    }

    public MifareClassicReadSuccessResponse(byte startBlock, byte endBlock, byte[] uid, byte[] data) {
        this.startBlock = startBlock;
        this.endBlock = endBlock;
        this.uid = uid;
        this.data = data;
    }

    /**
     * Retrieve the block to read from
     * @return
     */
    public byte getStartBlock() {
        return startBlock;
    }

    /**
     * Set the block read from
     * @param startBlock
     */
    public void setStartBlock(byte startBlock) {
        this.startBlock = startBlock;
    }

    /**
     * Retrieve the block reading stopped at
     * @return
     */
    public byte getEndBlock() {
        return endBlock;
    }

    /**
     * Set the block reading stopped at
     * @param endBlock
     */
    public void setEndBlock(byte endBlock) {
        this.endBlock = endBlock;
    }

    /**
     * The UID of the tag that was read from
     * @return 4 or 7 byte uid
     */
    public byte[] getUid() {
        return uid;
    }

    /**
     * Set the UID of the tag read from
     * @param uid 4 or 7 byte uid
     */
    public void setUid(byte[] uid) {
        this.uid = uid;
    }

    /**
     * Get the data that was read from the tag
     * @return 0 or more bytes that were read from the tag
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Set the data that was read from the tag
     * @param data 0 or more bytes read from the tag
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length == 0) {
            throw new MalformedPayloadException("Payload empty");
        }

        int uidLength = payload[0] & 0xff;
        if(payload.length < (uidLength+3)) {
            throw new MalformedPayloadException("Payload too short for uid type");
        }

        uid = Arrays.copyOfRange(payload,1,1+uidLength);
        int uidEnd = uidLength;
        startBlock = payload[uidEnd+1];
        endBlock = payload[uidEnd+2];
        if(payload.length > (uidEnd+2)) {
            data = Arrays.copyOfRange(payload, uidEnd + 3, payload.length);
        }
        else {
            data = new byte[0];
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte uidLength = (byte) uid.length;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(uidLength);
        try {
            outputStream.write(uid);
            outputStream.write(startBlock);
            outputStream.write(endBlock);
            outputStream.write(data);
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
