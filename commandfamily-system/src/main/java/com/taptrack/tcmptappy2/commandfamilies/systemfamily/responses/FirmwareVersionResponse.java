package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardLibraryVersionResponse;
import com.taptrack.tcmptappy2.StandardLibraryVersionResponseDelegate;

/**
 * Response containing the firmware version running on the connected Tappy
 */
public class FirmwareVersionResponse extends AbstractSystemMessage implements
        StandardLibraryVersionResponse {
    public static final byte COMMAND_CODE = 0x06;
    private StandardLibraryVersionResponseDelegate delegate;

    public FirmwareVersionResponse() {
        delegate = new StandardLibraryVersionResponseDelegate();
    }
    public FirmwareVersionResponse(byte majorVersion, byte minorVersion) {
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
