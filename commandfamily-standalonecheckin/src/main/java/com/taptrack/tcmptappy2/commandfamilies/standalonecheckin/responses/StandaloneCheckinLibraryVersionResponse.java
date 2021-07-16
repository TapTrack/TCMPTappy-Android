package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardLibraryVersionResponse;
import com.taptrack.tcmptappy2.StandardLibraryVersionResponseDelegate;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;

public class StandaloneCheckinLibraryVersionResponse extends AbstractStandaloneCheckinMessage implements StandardLibraryVersionResponse {
    public static final byte COMMAND_CODE = 0x0B;
    protected StandardLibraryVersionResponseDelegate delegate;

    public StandaloneCheckinLibraryVersionResponse() {
        delegate = new StandardLibraryVersionResponseDelegate();
    }

    public StandaloneCheckinLibraryVersionResponse(byte majorVersion, byte minorVersion) {
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
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        delegate.parsePayload(payload);
    }

    @Override
    public byte[] getPayload() {
        return delegate.getPayload();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
