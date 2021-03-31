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

package com.taptrack.tcmptappy2.commandfamilies.basicnfc.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses.ScanTimeoutResponse;

public abstract class AbstractDispatchTagCommand extends AbstractBasicNfcMessage {
    private byte timeout;

    AbstractDispatchTagCommand() {
        this((byte)0x00);
    }

    AbstractDispatchTagCommand(byte timeout) {
        super();
        this.timeout = timeout;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if (payload.length < 1) {
            throw new MalformedPayloadException("Payload too short");
        }
        this.timeout = payload[0];
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return new byte[]{timeout};
    }

    /**
     * Set the timeout after which the Tappy will stop scanning and send a
     * {@link ScanTimeoutResponse}
     * <p>
     * 0x00 disables timeout
     *
     * @param timeout
     */
    public void setTimeout(byte timeout) {
        this.timeout = timeout;
    }

    /**
     * Retreive the timeout after which the Tappy will stop scanning and send a
     * {@link ScanTimeoutResponse}
     * <p>
     * 0x00 disables timeout
     *
     * @return
     */
    public byte getTimeout() {
        return timeout;
    }
}
