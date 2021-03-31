package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.AbstractMifareClassicMessage;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.KeySetting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Instructs the Tappy to read bytes from a MIFARE Classic
 */
public class ReadMifareClassicCommand extends AbstractMifareClassicMessage {
    public static final byte COMMAND_CODE = 0x01;

    protected byte timeout;
    protected byte startBlock;
    protected byte endBlock;
    protected byte keySetting;
    protected byte[] key;

    public ReadMifareClassicCommand() {
        timeout = 0x01;
        startBlock = 0x03;
        endBlock = 0x05;
        keySetting = KeySetting.KEY_A;
        key = new byte[6];
    }

    /**
     * Create a Read MIFARE Classic command
     * @param timeout the time to wait before the command times out, 0x00 disables timeout
     * @param startBlock the block to start reading from
     * @param endBlock the block to end reading from
     * @param keySetting which key to authenticate with see {@link KeySetting}
     * @param key the 6 byte key to authenticate with
     * @throws IllegalArgumentException if the key is invalid
     */
    public ReadMifareClassicCommand(byte timeout, byte startBlock, byte endBlock, byte keySetting, byte[] key) throws IllegalArgumentException {
        this.timeout = timeout;
        this.startBlock = startBlock;
        this.endBlock = endBlock;
        this.keySetting = keySetting;
        if(key.length != 6)
            throw new IllegalArgumentException("Key length must be 6");
        this.key = key;
    }

    /**
     * Set the timeout after which the Tappy will stop scanning
     *
     * 0x00 disables timeout
     * @param timeout
     */
    public void setTimeout(byte timeout) {
        this.timeout = timeout;
    }

    /**
     * Retreive the timeout after which the Tappy will stop scanning
     *
     * 0x00 disables timeout
     * @return
     */
    public byte getTimeout() {
        return timeout;
    }

    /**
     * Retrieve the block to start reading from
     * @return
     */
    public byte getStartBlock() {
        return startBlock;
    }

    /**
     * Set the block this command will start reading from
     * @param startBlock
     */
    public void setStartBlock(byte startBlock) {
        this.startBlock = startBlock;
    }

    /**
     * Retrieve the block to stop reading at
     * @return
     */
    public byte getEndBlock() {
        return endBlock;
    }

    /**
     * Set the block this command will stop reading from
     * @param endBlock
     */
    public void setEndBlock(byte endBlock) {
        this.endBlock = endBlock;
    }

    /**
     * Determine what key this command will attempt to authenticate with
     *
     * See {@link KeySetting}
     * @return
     */
    public byte getKeySetting() {
        return keySetting;
    }

    /**
     * Set which key to authenticate with
     * See {@link KeySetting}
     * @param keySetting
     */
    public void setKeySetting(byte keySetting) {
        this.keySetting = keySetting;
    }

    /**
     * Retrieve the authentication key this command will attempt to use
     * @return 6 byte Crypto-1 Key
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * Set the key this command will use for authenticateion
     * @param key 6 byte Crypto-1 key
     * @throws IllegalArgumentException Key was not 6 bytes long
     */
    public void setKey(byte[] key) throws IllegalArgumentException {
        if(key.length != 6)
            throw new IllegalArgumentException("Key length must be 6 bytes");
        this.key = key;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length != 10)
            throw new MalformedPayloadException();
        timeout = payload[0];
        startBlock = payload[1];
        endBlock = payload[2];
        keySetting = payload[3];
        key = Arrays.copyOfRange(payload, 4, payload.length);
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(10);
        out.write(timeout);
        out.write(startBlock);
        out.write(endBlock);
        out.write(keySetting);
        try {
            out.write(key);
        } catch (IOException e) {
            throw new IllegalStateException("This should be impossible");
        }
        return out.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
