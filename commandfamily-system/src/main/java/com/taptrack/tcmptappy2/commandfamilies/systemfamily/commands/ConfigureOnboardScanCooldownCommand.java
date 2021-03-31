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

package com.taptrack.tcmptappy2.commandfamilies.systemfamily.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Configure the onboard scan cooldown feature of a Tappy.
 * Note that the Tappy's firmware must support scan cooldown
 * for this command to have any effect.
 *
 * You can use this command to change whether the Tappy with operate with
 * a cooldown for reporting repeated scans
 */
public class ConfigureOnboardScanCooldownCommand extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = (byte)0x08;
    private byte cooldownSetting;
    private int bufferSize;


    public interface CooldownSettings {
        /**
         * NO_CHANGE tells the Tappy to use whatever setting it currently has
         */
        byte NO_CHANGE = 0x00;

        /**
         * ENABLE_COOLDOWN causes the Tappy to ignore repeated scans of the same tag
         */
        byte ENABLE_COOLDOWN= 0x01;

        /**
         * DISABLE_DUAL_POLLING causes the Tappy to stop ignoring repeated scans of the
         * same tag
         */
        byte DISABLE_COOLDOWN = 0x02;
    }

    public ConfigureOnboardScanCooldownCommand() {
        cooldownSetting = CooldownSettings.NO_CHANGE;
        bufferSize = 10;
    }

    /**
     * ConfigureOnboardScanCooldownCommand configures the cooldown behaviour of a cooldown-capable Tappy
     *
     * @param cooldownSetting The desired cooldown mode. {@see CooldownSettings}
     * @param bufferSize The maximum number of tappies that the buffer. Maximum value is 10, and a
     *                   value of 0 affects no change
     *
     * @throws IllegalArgumentException when a buffer size greater than 10 or a negative value is
     * specified
     */
    public ConfigureOnboardScanCooldownCommand(byte cooldownSetting,
                                               int bufferSize) {
        this.cooldownSetting = cooldownSetting;

        if (bufferSize > 10 || bufferSize < 0) {
            throw new IllegalArgumentException("the buffer size must be between 0 and 10, inclusive");
        } else {
            this.bufferSize = bufferSize;
        }
    }

    public byte getCooldownSetting() {
        return cooldownSetting;
    }

    public void setCooldownSetting(byte cooldownSetting) {
        this.cooldownSetting = cooldownSetting;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        if (bufferSize > 10 || bufferSize < 0) {
            throw new IllegalArgumentException("the buffer size must be between 0 and 10, inclusive");
        } else {
            this.bufferSize = bufferSize;
        }
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 3) {
            throw new IllegalArgumentException("Payload too short");
        }

        this.cooldownSetting = payload[0];

        int tempBuf = Utils.uint16ToInt(new byte[]{payload[1],payload[2]});
        if (tempBuf > 10 || tempBuf < 0) {
            throw new IllegalArgumentException("payload specified an invalid value");
        }
        this.bufferSize = tempBuf;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(4);
        stream.write(cooldownSetting);
        try {
            stream.write(Utils.intToUint16(this.bufferSize));
        } catch (IOException e) {
            // impossible
        }

        return stream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
