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

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy.tappy.constants.TagTypes;
import com.taptrack.tcmptappy2.MalformedPayloadException;;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * The Tappy has found a tag with ndef-formatted data
 */
public class NdefFoundResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x02;
    byte[] tagCode;
    byte tagType;
    NdefMessage message;

    public NdefFoundResponse() {
        tagCode = new byte[7];
        message = new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY,null,null,null));
        tagType = TagTypes.TAG_UNKNOWN;
    }

    public NdefFoundResponse(byte[] tagCode, byte tagType, NdefMessage message) {
        this.tagCode = tagCode;
        this.tagType = tagType;
        this.message = message;
    }

    public byte[] getTagCode() {
        return tagCode;
    }

    public void setTagCode(byte[] tagCode) {
        this.tagCode = tagCode;
    }

    public byte getTagType() {
        return tagType;
    }

    public void setTagType(byte tagType) {
        this.tagType = tagType;
    }

    public NdefMessage getMessage() {
        return message;
    }

    public void setMessage(NdefMessage message) {
        this.message = message;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length < 2) throw new MalformedPayloadException("No control bytes");

        tagType = payload[0];
        byte tagCodeLength = (byte) (payload[1] & 0xff);
        tagCode = Arrays.copyOfRange(payload, 2, tagCodeLength + 2);
        byte[] ndefMessage = new byte[payload.length - (tagCodeLength + 2)];

        //make sure ndef payload is not zero-length
        if(payload.length > ((tagCodeLength & 0xff)+2)) {
            System.arraycopy(payload,tagCodeLength+2,ndefMessage,0,(payload.length - tagCodeLength - 2));
        }
        if(ndefMessage.length != 0) {
            try {
                message = new NdefMessage(ndefMessage);
            } catch (FormatException e) {
                e.printStackTrace();
                throw new MalformedPayloadException("Bad Ndef Format");
            }
        }
        else {
            message = new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY,null,null,null));
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte[] messageBytes = message.toByteArray();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2+ tagCode.length+messageBytes.length);
        outputStream.write(tagType);
        outputStream.write(tagCode.length);
        try {
            outputStream.write(tagCode);
            outputStream.write(messageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] result = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
