package com.taptrack.tcmptappy2;

public interface StandardErrorResponse {
    byte getErrorCode();
    void setErrorCode(byte errorCode);
    byte getInternalErrorCode();
    void setInternalErrorCode(byte internalErrorCode);
    byte getReaderStatus();
    void setReaderStatus(byte status);
    String getErrorMessage();
    void setErrorMessage(byte[] message);
    void setErrorMessage(String message);
    void parsePayload(byte[] payload) throws MalformedPayloadException;
    byte[] getPayload();
}
