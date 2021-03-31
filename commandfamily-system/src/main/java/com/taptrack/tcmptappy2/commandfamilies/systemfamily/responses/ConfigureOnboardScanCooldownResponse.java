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

package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The Tappy has successfully set onboard scan cooldowns
 */
public class ConfigureOnboardScanCooldownResponse extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = (byte) 0x0D;

    private boolean cooldownEnabled;
    private int bufferSize;

    public ConfigureOnboardScanCooldownResponse() {

    }

    public ConfigureOnboardScanCooldownResponse(boolean cooldownEnabled, int bufferSize) {
        if (bufferSize > 10 || bufferSize < 0) {
            throw new IllegalArgumentException("buffer size must be between 10 and 0 inclusive");
        }

        this.cooldownEnabled = cooldownEnabled;
        this.bufferSize = bufferSize;
    }

    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    public void setCooldownEnabled(boolean cooldownEnabled) {
        this.cooldownEnabled = cooldownEnabled;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * The buffer size that was set
     * @param bufferSize the buffer size the tappy is using, must be between 0 and 10 exclusive
     *
     * @throws IllegalArgumentException if specified buffer size is greater than ten or less than 0
     */
    public void setBufferSize(int bufferSize) {
        if (bufferSize > 10 || bufferSize < 0) {
            throw new IllegalArgumentException("buffer size must be between 0 and 10 inclusive");
        }
        this.bufferSize = bufferSize;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if (payload.length < 3) {
            throw new IllegalArgumentException("payload must be at least three bytes long");
        }

        int tempBuf = Utils.uint16ToInt(new byte[]{payload[1],payload[2]});
        if (tempBuf > 10 || tempBuf < 0) {
            throw new IllegalArgumentException("buffer size must be between 0 and 10 inclusive");
        }

        this.cooldownEnabled = payload[0] == 0x01;
        this.bufferSize = tempBuf;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(3);
        byteArrayOutputStream.write(cooldownEnabled ? 0x01 : 0x00);
        try {
            byteArrayOutputStream.write(Utils.intToUint16(bufferSize));
        } catch (IOException e) {
            // impossible
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
