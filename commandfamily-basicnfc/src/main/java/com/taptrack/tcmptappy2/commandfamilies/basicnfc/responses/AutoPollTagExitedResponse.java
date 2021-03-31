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

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AutoPollingConstants;

import java.util.Arrays;


/**
 * A tag has entered the detection range of a Tappy configured for
 * auto-polling
 *
 * The detected tag type will be one of the types specified in
 * {@link AutoPollingConstants.ResponseTagTypes}
 *
 * The format of the metadata will depend on the type of tag detected.
 * See the Tappy Command Reference for information on the format of this data.
 */
public class AutoPollTagExitedResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x0D;

    private byte detectedTagType;
    private byte[] tagMetadata;

    public AutoPollTagExitedResponse() {
        detectedTagType = AutoPollingConstants.ResponseTagTypes.UNRECOGNIZED_TYPE;
        tagMetadata = new byte[0];
    }

    public AutoPollTagExitedResponse(byte detectedTagType, byte[] tagMetadata) {
        this.detectedTagType = detectedTagType;
        this.tagMetadata = tagMetadata;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 1) //at least a tag type and a byte of metadata
            throw new MalformedPayloadException();
        detectedTagType  = payload[0];

        if (payload.length >= 2) {
            tagMetadata = Arrays.copyOfRange(payload, 1, payload.length);
        } else {
            tagMetadata = new byte[0];
        }
    }

    public byte getDetectedTagType() {
        return detectedTagType;
    }

    public void setDetectedTagType(byte detectedTagType) {
        this.detectedTagType = detectedTagType;
    }

    public byte[] getTagMetadata() {
        return tagMetadata;
    }

    public void setTagMetadata(byte[] tagMetadata) {
        this.tagMetadata = tagMetadata;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte[] payload = new byte[tagMetadata.length+1];
        payload[0] = detectedTagType;
        System.arraycopy(tagMetadata,0,payload,1,tagMetadata.length);
        return payload;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
