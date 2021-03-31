package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardLibraryVersionResponse;
import com.taptrack.tcmptappy2.StandardLibraryVersionResponseDelegate;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.AbstractMifareClassicMessage;

public class MifareClassicLibraryVersionResponse extends AbstractMifareClassicMessage implements
        StandardLibraryVersionResponse {
    public static final byte COMMAND_CODE = 0x04;
    protected StandardLibraryVersionResponseDelegate delegate;

    public MifareClassicLibraryVersionResponse() {
        delegate = new StandardLibraryVersionResponseDelegate();
    }

    public MifareClassicLibraryVersionResponse(byte majorVersion, byte minorVersion) {
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
