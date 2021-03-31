/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import android.nfc.FormatException;
import android.nfc.NdefMessage;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.LockingModes;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ScanTimeoutResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Command the Tappy to write an arbitrary custom NDEF record to tags
 */
public class WriteNdefCustomMessageCommand extends AbstractBasicNfcMessage {

    public static final byte COMMAND_CODE = (byte)0x07;
    protected byte timeout;
    protected byte lockflag; //1 to lock the flag
    protected byte[] content;

    public WriteNdefCustomMessageCommand() {
        timeout = (byte) 0x00;
        lockflag = (byte) 0x00;
        content = new byte[0];
    }


    public WriteNdefCustomMessageCommand(byte timeout, boolean lockTag, byte[] content) {
        this.timeout = timeout;
        this.lockflag = (byte) (lockTag ? 0x01: 0x00);
        this.content = content;
    }

    public WriteNdefCustomMessageCommand(byte timeout, byte lockTag, byte[] content) {
        this.timeout = timeout;
        this.lockflag = lockTag;
        this.content = content;
    }

    public WriteNdefCustomMessageCommand(byte timeout, byte lockTag, NdefMessage content) {
        this.timeout = timeout;
        this.lockflag = lockTag;
        this.content =  content.toByteArray();
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length >= 2) {
            timeout = payload[0];
            lockflag = payload[1];
            if(payload.length > 2) {
                content = Arrays.copyOfRange(payload, 2, payload.length);
            }
            else {
                content = new byte[0];
            }
        }
        else {
            throw new MalformedPayloadException("Invalid raw message");
        }
    }

    /**
     * Retreive the timeout after which the Tappy will stop scanning and send a
     * {@link ScanTimeoutResponse}
     *
     * 0x00 disables timeout
     * @return
     */
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

    /**
     * Get the flag that determines if the Tappy will attempt to lock the tag after writing
     *
     * See: {@link LockingModes}
     * @return locking flag
     */
    public byte getLockflag() {
        return lockflag;
    }

    /**
     * Set the flag that determines if the Tappy will attempt to lock the tag after writing
     *
     * See: {@link LockingModes}
     * @param lockflag locking mode this command should be executed with
     */
    public void setLockflag(byte lockflag) {
        this.lockflag = lockflag;
    }

    /**
     * Get the byte array that represents this command's custom content
     * @return
     */
    public byte[] getContentBytes() {
        return content;
    }

    /**
     * Get the NdefMessage this will attempt to write
     * @return Current ndef message or null if no message currently set
     * @throws FormatException if the current content is not a valid {@link NdefMessage}
     */
    public NdefMessage getContent() throws FormatException {
        if(content == null || content.length == 0) {
            return null;
        }
        else {
            return new NdefMessage(content);
        }
    }

    public void setContent(NdefMessage content) {
        this.content = content.toByteArray();
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * If the current state of the locking tag will lock the tag
     * @return
     */
    public boolean willLock() {
        return lockflag == LockingModes.LOCK_TAG;
    }

    /**
     * Set locking flag to the appropriate value for a locking state
     * @param lockTag
     */
    public void setToLock(boolean lockTag) {
        this.lockflag = (byte) (lockTag ? LockingModes.LOCK_TAG:LockingModes.DONT_LOCK);
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(3+ content.length);
        outputStream.write(timeout);
        outputStream.write(lockflag);
        try {
            outputStream.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] result = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
