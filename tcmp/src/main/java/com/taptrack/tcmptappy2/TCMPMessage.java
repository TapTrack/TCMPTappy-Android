package com.taptrack.tcmptappy2;

public interface TCMPMessage {
    void parsePayload(byte[] payload) throws MalformedPayloadException;
    byte[] getPayload();
    byte getCommandCode();
    byte[] getCommandFamily();
    byte[] toByteArray();
}
