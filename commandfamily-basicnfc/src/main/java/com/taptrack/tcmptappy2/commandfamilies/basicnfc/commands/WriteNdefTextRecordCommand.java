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

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.LockingModes;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ScanTimeoutResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class WriteNdefTextRecordCommand extends AbstractBasicNfcMessage {

    public static final byte COMMAND_CODE = (byte)0x06;
    protected byte timeout;
    protected byte lockflag; //1 to lock the flag
    protected byte[] text;

    public WriteNdefTextRecordCommand() {
        timeout = (byte) 0x00;
        lockflag = (byte) 0x00;
        text = new byte[0];
    }

    public WriteNdefTextRecordCommand(byte timeout, boolean lockTag, byte[] text) {
        this.timeout = timeout;
        this.lockflag = (byte) (lockTag ? 0x01: 0x00);
        this.text = text;
    }

    public WriteNdefTextRecordCommand(byte timeout, byte lockTag, byte[] text) {
        this.timeout = timeout;
        this.lockflag = lockTag;
        this.text = text;
    }

    public WriteNdefTextRecordCommand(byte timeout, byte lockTag, String text) {
        this(timeout, lockTag, text.getBytes());
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length >= 2) {
            timeout = payload[0];
            lockflag = payload[1];
            if(payload.length > 2) {
                text = Arrays.copyOfRange(payload, 2, payload.length);
            }
            else {
                text = new byte[0];
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


    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(3+ text.length);
        outputStream.write(timeout);
        outputStream.write(lockflag);
        try {
            outputStream.write(text);
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
