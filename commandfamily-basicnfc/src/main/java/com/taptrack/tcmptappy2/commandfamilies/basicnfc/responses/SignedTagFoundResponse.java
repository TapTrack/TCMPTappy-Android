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
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.TlvParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A tag has been found by the Tappy
 */
public class SignedTagFoundResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x0B;
    private static final byte TAG_FOUND_TLV_TYPE = 0x01;

    byte[] mTagCode;
    byte mTagType;

    public SignedTagFoundResponse() {
        mTagCode = new byte[7];
        mTagType = TagTypes.TAG_UNKNOWN;
    }

    public SignedTagFoundResponse(byte[] tagCode, byte tagType) {
        mTagCode = tagCode;
        mTagType = tagType;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 7) // a tlv containing at least a 4 byte uid
            throw new MalformedPayloadException();

        try {
            List<TlvParser.Tlv> parsedTlvs = TlvParser.parseTlvsFromBinary(payload);
            if(parsedTlvs.size() == 0) {
                throw new MalformedPayloadException("No TLVs found in payload");
            }

            for(int i = 0; i < parsedTlvs.size(); i++) {
                TlvParser.Tlv tlv = parsedTlvs.get(i);
                if(tlv.getType() == TAG_FOUND_TLV_TYPE) {
                    byte[] value = tlv.getValue();
                    if(value.length < 5) {
                        throw new MalformedPayloadException("TLV too short to contain data");
                    }

                    mTagType = value[0];
                    mTagCode = Arrays.copyOfRange(value,1,value.length);
                    return;
                }
            }

            throw new MalformedPayloadException("Missing TLV in payload");
        } catch (IllegalArgumentException e) {
            throw new MalformedPayloadException(e);
        }
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
        byte[] data = new byte[mTagCode.length+1];
        data[0] = mTagType;
        System.arraycopy(mTagCode,0,data,1,mTagCode.length);
        TlvParser.Tlv tlv = new TlvParser.Tlv(TAG_FOUND_TLV_TYPE,data);
        return TlvParser.composeBinaryFromTlvs(Collections.singletonList(tlv));
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
