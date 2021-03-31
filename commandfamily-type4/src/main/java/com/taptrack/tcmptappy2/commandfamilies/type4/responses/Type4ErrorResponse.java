package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardErrorResponse;
import com.taptrack.tcmptappy2.StandardErrorResponseDelegate;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * This class is response is sent by the Tappy when an error is
 * encountered performing a Type4 command.
 *
 * See also {@link Type4PollingErrorResponse} for a different
 * error response that can be returned in a more specific circumstance.
 */
public class Type4ErrorResponse extends AbstractType4Message implements StandardErrorResponse {
    public static final byte COMMAND_CODE = 0x7F;

    public interface ErrorCodes {
        byte TOO_FEW_PARAMETERS = 0x01;
        byte TOO_MANY_PARAMETERS = 0x02;
        byte TRANSCEIVE_ERROR = 0x03;
        byte INVALID_PARAMETER = 0x04;
        /**
         * This occurs if the Tappy detects that there are no tags activated in its
         * field when a transceive is requested
         */
        byte NO_TAG_PRESENT = 0x05;
        /**
         * Occurs when there was an error requesting general status information
         * before a transceive command
         */
        byte NFC_CHIP_ERROR = 0x06;

        /**
         * Occurs when the active reader is successfully detected by the Tappy, but
         * an error occured when the Tappy requested the first command APDU
         */
        byte REQUEST_FOR_INITIATOR_FIRST_COMMAND_FAILED = 0x07;
    }

    private StandardErrorResponseDelegate delegate;

    public Type4ErrorResponse() {
        delegate = new StandardErrorResponseDelegate();
    }

    public Type4ErrorResponse(byte errorCode, byte internalErrorCode, byte readerStatus, String errorMessage) {
        delegate = new StandardErrorResponseDelegate(errorCode,internalErrorCode,readerStatus,errorMessage);
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
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
