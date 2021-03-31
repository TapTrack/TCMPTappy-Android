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
 * The CRC on a command the Tappy received did not match what it expected
 *
 * This likely indicates some sort of data corruption in transmission
 */
public class CrcMismatchErrorResponse extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = 0x03;
    protected byte[] mErrorMessage;

    public CrcMismatchErrorResponse() {
        mErrorMessage = new byte[0];
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        mErrorMessage = payload;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return mErrorMessage;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
