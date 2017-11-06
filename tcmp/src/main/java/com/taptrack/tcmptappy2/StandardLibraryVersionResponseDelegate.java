package com.taptrack.tcmptappy2;

public class StandardLibraryVersionResponseDelegate implements StandardLibraryVersionResponse {
    private byte majorVersion;
    private byte minorVersion;

    public StandardLibraryVersionResponseDelegate() {
        majorVersion = 0x00;
        minorVersion = 0x00;
    }

    public StandardLibraryVersionResponseDelegate(byte majorVersion, byte minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    @Override
    public byte getMajorVersion() {
        return majorVersion;
    }

    @Override
    public void setMajorVersion(byte majorVersion) {
        this.majorVersion = majorVersion;
    }

    @Override
    public byte getMinorVersion() {
        return minorVersion;
    }

    @Override
    public void setMinorVersion(byte minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 2)
            throw new MalformedPayloadException("Version payload must be two bytes");
        majorVersion = payload[0];
        minorVersion = payload[1];
    }

    @Override
    public byte[] getPayload() {
        return new byte[]{majorVersion,minorVersion};
    }
}
