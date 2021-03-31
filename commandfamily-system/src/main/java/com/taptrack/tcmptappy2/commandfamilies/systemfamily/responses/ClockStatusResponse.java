/*
 * Copyright (c) 2021. Papyrus Electronics, Inc
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

/**
 * Response for a Tappy indicating whether the clock is being driven by an external crystal
 * or if the clock is based on an internal timer.
 */
public class ClockStatusResponse extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = (byte)0x0B;

    private boolean usingRtcc;

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if (payload.length != 1) {
            throw new MalformedPayloadException("Payload too short");
        }
        usingRtcc = payload[0] != 0x00;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte rtccByte = (byte)(usingRtcc ? 0x01 : 0x00);
        return new byte[]{rtccByte};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }

    /**
     * @return {@code true} if the clock is driven by an external crystal. {@code false} otherwise.
     */
    public boolean isUsingRtcc() {
        return usingRtcc;
    }
}
