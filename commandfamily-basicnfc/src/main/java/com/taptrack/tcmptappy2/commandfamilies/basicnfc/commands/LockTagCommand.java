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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Tell the Tappy to lock a tag
 *
 * A timeout of the number of seconds to wait can be provided,
 * as well as a tag code if a specific tag is desired to be locked
 * with the Tappy ignoring all others
 */
public class LockTagCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = (byte)0x08;

    protected byte timeout;
    protected byte[] tagCode;

    public LockTagCommand() {
        timeout = 0x00;
        tagCode = new byte[0];
    }

    /**
     * Lock a specific tag
     * @param timeout number of seconds to wait for a tag, 0 disables
     * @param tagCode tag code for specific tag to lock, 0 length disables
     */
    public LockTagCommand(byte timeout, byte[] tagCode) {
        this.timeout = timeout;
        this.tagCode = tagCode;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length >= 2) {
            int uidLength = payload[1];
            if((uidLength+2) < payload.length) {
                throw new MalformedPayloadException("Payload too short to contain uid of specified length");
            } else {
                this.tagCode = Arrays.copyOfRange(payload,2,2+uidLength);
                this.timeout = payload[0];
            }
        } else {
            throw new MalformedPayloadException("Payload too short to contain a lock tag command");
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        if(tagCode == null || tagCode.length == 0) {
            return new byte[]{timeout,0x00};
        } else {
            ByteArrayOutputStream outputStream
                    = new ByteArrayOutputStream(2+tagCode.length);
            outputStream.write(timeout);
            outputStream.write((byte) tagCode.length);
            try {
                outputStream.write(tagCode);
            } catch (IOException ignored) {
            }
            return outputStream.toByteArray();
        }
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }

    public byte getTimeout() {
        return timeout;
    }

    public void setTimeout(byte timeout) {
        this.timeout = timeout;
    }

    public byte[] getTagCode() {
        return tagCode;
    }

    public void setTagCode(byte[] tagCode) {
        this.tagCode = tagCode;
    }
}
