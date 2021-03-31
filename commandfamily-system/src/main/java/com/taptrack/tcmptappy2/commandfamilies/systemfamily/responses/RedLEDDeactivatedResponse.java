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

public class RedLEDDeactivatedResponse extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = (byte)0x0F;
    private boolean redLEDActive;
    private boolean greenLEDActive;
    private boolean blueLEDActive;
    private boolean buzzerActive;

    public RedLEDDeactivatedResponse() {
        redLEDActive = greenLEDActive = blueLEDActive = buzzerActive = false;
    }

    public RedLEDDeactivatedResponse(
        boolean redLEDActive,
        boolean greenLEDActive,
        boolean blueLEDActive,
        boolean buzzerActive
    ) {
        this.redLEDActive = redLEDActive;
        this.greenLEDActive = greenLEDActive;
        this.blueLEDActive = blueLEDActive;
        this.buzzerActive = buzzerActive;
    }

    public boolean isRedLEDActive() {
        return redLEDActive;
    }

    public void setRedLEDActive(boolean redLEDActive) {
        this.redLEDActive = redLEDActive;
    }

    public boolean isGreenLEDActive() {
        return greenLEDActive;
    }

    public void setGreenLEDActive(boolean greenLEDActive) {
        this.greenLEDActive = greenLEDActive;
    }

    public boolean isBlueLEDActive() {
        return blueLEDActive;
    }

    public void setBlueLEDActive(boolean blueLEDActive) {
        this.blueLEDActive = blueLEDActive;
    }

    public boolean isBuzzerActive() {
        return buzzerActive;
    }

    public void setBuzzerActive(boolean buzzerActive) {
        this.buzzerActive = buzzerActive;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if (payload.length < 4) {
            throw new MalformedPayloadException("payload was too short to contain data");
        }

        redLEDActive = payload[0] == 0x01;
        greenLEDActive = payload[1] == 0x01;
        blueLEDActive = payload[2] == 0x01;
        buzzerActive= payload[3] == 0x01;

    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return new byte[]{
                (byte)(redLEDActive ? 0x01 : 0x00),
                (byte)(greenLEDActive ? 0x01 : 0x00),
                (byte)(blueLEDActive ? 0x01 : 0x00),
                (byte)(buzzerActive ? 0x01 : 0x00)
        };
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
