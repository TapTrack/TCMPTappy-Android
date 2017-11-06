package com.taptrack.tcmptappy2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class StandardErrorResponseDelegate implements StandardErrorResponse {
    private byte errorCode;
    private byte internalErrorCode;
    private byte readerStatus;
    private String errorMessage;

    public StandardErrorResponseDelegate() {
        this.errorCode = 0x00;
        this.internalErrorCode = 0x00;
        this.readerStatus = 0x00;
        this.errorMessage = "";
    }

    public StandardErrorResponseDelegate(byte errorCode, byte internalErrorCode, byte readerStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.internalErrorCode = internalErrorCode;
        this.readerStatus = readerStatus;
        this.errorMessage = errorMessage;
    }

    public StandardErrorResponseDelegate(byte errorCode, byte internalErrorCode, byte readerStatus, byte[] errorMessage) {
        this(errorCode,internalErrorCode,readerStatus,new String(errorMessage));
    }

    @Override
    public byte getErrorCode() {
        return errorCode;
    }

    @Override
    public void setErrorCode(byte errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public byte getInternalErrorCode() {
        return internalErrorCode;
    }

    @Override
    public void setInternalErrorCode(byte internalErrorCode) {
        this.internalErrorCode = internalErrorCode;
    }

    @Override
    public byte getReaderStatus() {
        return readerStatus;
    }

    @Override
    public void setReaderStatus(byte readerStatus) {
        this.readerStatus = readerStatus;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void setErrorMessage(byte[] errorMessage) {
        this.errorMessage = new String(errorMessage);
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length >= 3) {
            errorCode = (byte) (payload[0] & 0xff);
            internalErrorCode = (byte) (payload[1] & 0xff);
            readerStatus = (byte) (payload[2] & 0xff);
            if(payload.length > 3) {
                byte[] message = Arrays.copyOfRange(payload, 3, payload.length);
                errorMessage = new String(message);
            }
            else {
                errorMessage = "";
            }

        }
        else {
            throw new MalformedPayloadException("Payload too short");
        }
    }

    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(3+errorMessage.length());
        outputStream.write(errorCode);
        outputStream.write(internalErrorCode);
        outputStream.write(readerStatus);
        try {
            outputStream.write(errorMessage.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] payload = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payload;
    }
}
