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

package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy.tappy.constants.TagTypes;
import com.taptrack.tcmptappy2.MalformedPayloadException;;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;

import java.util.Arrays;

/**
 * A tag has been found by the Tappy
 */
public class TagFoundResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x01;
    byte[] mTagCode;
    byte mTagType;

    public TagFoundResponse() {
        mTagCode = new byte[7];
        mTagType = TagTypes.TAG_UNKNOWN;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 5) //at least a 4 byte uid
            throw new MalformedPayloadException();
        mTagType  = payload[0];
        mTagCode = Arrays.copyOfRange(payload, 1, payload.length);
    }

    public TagFoundResponse(byte[] tagCode, byte tagType) {
        mTagCode = tagCode;
        mTagType = tagType;
    }

    public byte[] getTagCode() {
        return mTagCode;
    }

    public byte getTagType() {
        return mTagType;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte[] payload = new byte[mTagCode.length+1];
        payload[0] = mTagType;
        System.arraycopy(mTagCode,0,payload,1,mTagCode.length);
        return payload;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
