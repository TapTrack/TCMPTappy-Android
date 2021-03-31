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

import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage;
import com.taptrack.tcmptappy2.MalformedPayloadException;

/**
 * Response for a Tappy indicating its current battery level
 *
 * Has 5% resolution. In other words, a Tappy charging from 90%
 * to full will report 90%, 95%, and 100%, but it will never report
 * 91, 92, 93, 94, 96, 97, 98, or 99%.
 */
public class GetBatteryLevelResponse extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = (byte) 0x08;
    private byte batteryLevel;

    public GetBatteryLevelResponse() {
        batteryLevel = 0x00;
    }

    public GetBatteryLevelResponse(byte batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setBatteryLevel(byte level) {
        this.batteryLevel = level;
    }

    public byte getBatteryLevel() {
        return this.batteryLevel;
    }

    public int getBatteryLevelPercent() {
        return (batteryLevel & 0xFF);
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length != 1) throw new MalformedPayloadException("Payload too short");
        batteryLevel = payload[0];
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return new byte[]{batteryLevel};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}

