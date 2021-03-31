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

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardLibraryVersionResponse;
import com.taptrack.tcmptappy2.StandardLibraryVersionResponseDelegate;

;

public class BasicNfcLibraryVersionResponse extends AbstractBasicNfcMessage implements
        StandardLibraryVersionResponse {
    public static final byte COMMAND_CODE = 0x04;
    private StandardLibraryVersionResponseDelegate delegate;

    public BasicNfcLibraryVersionResponse() {
        delegate = new StandardLibraryVersionResponseDelegate();
    }

    public BasicNfcLibraryVersionResponse(byte majorVersion, byte minorVersion) {
        delegate = new StandardLibraryVersionResponseDelegate(majorVersion,minorVersion);
    }

    @Override
    public byte getMajorVersion() {
        return delegate.getMajorVersion();
    }

    @Override
    public void setMajorVersion(byte majorVersion) {
        delegate.setMajorVersion(majorVersion);
    }

    @Override
    public byte getMinorVersion() {
        return delegate.getMinorVersion();
    }

    @Override
    public void setMinorVersion(byte minorVersion) {
        delegate.setMinorVersion(minorVersion);
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        delegate.parsePayload(payload);
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return delegate.getPayload();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
