package com.taptrack.tcmptappy2.commandfamilies.systemfamily.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.commandfamilies.systemfamily.AbstractSystemMessage;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardErrorResponse;
import com.taptrack.tcmptappy2.StandardErrorResponseDelegate;

public class SystemErrorResponse extends AbstractSystemMessage implements StandardErrorResponse {
    public static final byte COMMAND_CODE = 0x7F;
    protected StandardErrorResponseDelegate delegate;

    public interface ErrorCodes {
        /**
         * A parameter was supplied for a command that is not valid
         */
        byte INVALID_PARAMETER = 0x05;
        /**
         * The tappy received a command included in a command family that it
         * does not support
         */
        byte UNSUPPORTED_COMMAND_FAMILY = 0x06;
        /**
         * Not enough parameters were specified to the command
         */
        byte TOO_FEW_PARAMETERS = 0x07;
        /**
         * The Tappy was unable to store the specified NVM configuration because it was too long
         */
        byte NVM_CONFIG_TOO_LONG = 0x0B;
    }

    public SystemErrorResponse() {
        delegate = new StandardErrorResponseDelegate();
    }

    public SystemErrorResponse(byte errorCode, byte internalErrorCode, byte readerStatus, String message){
        delegate = new StandardErrorResponseDelegate(errorCode, internalErrorCode, readerStatus, message);
    }

    @Override
    public byte getErrorCode() {
        return delegate.getErrorCode();
    }

    @Override
    public void setErrorCode(byte errorCode) {
        delegate.setErrorCode(errorCode);
    }

    @Override
    public byte getInternalErrorCode() {
        return delegate.getInternalErrorCode();
    }

    @Override
    public void setInternalErrorCode(byte internalErrorCode) {
        delegate.setInternalErrorCode(internalErrorCode);
    }

    @Override
    public byte getReaderStatus() {
        return delegate.getReaderStatus();
    }

    @Override
    public void setReaderStatus(byte readerStatus) {
        delegate.setReaderStatus(readerStatus);
    }

    @Override
    public String getErrorMessage() {
        return delegate.getErrorMessage();
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        delegate.setErrorMessage(errorMessage);
    }

    @Override
    public void setErrorMessage(byte[] errorMessage) {
        delegate.setErrorMessage(errorMessage);
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
    public byte getCommandCode(){
        return COMMAND_CODE;
    }

}
