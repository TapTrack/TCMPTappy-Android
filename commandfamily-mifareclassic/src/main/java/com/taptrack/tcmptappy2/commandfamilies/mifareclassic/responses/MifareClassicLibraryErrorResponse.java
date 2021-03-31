package com.taptrack.tcmptappy2.commandfamilies.mifareclassic.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardErrorResponse;
import com.taptrack.tcmptappy2.StandardErrorResponseDelegate;
import com.taptrack.tcmptappy2.commandfamilies.mifareclassic.AbstractMifareClassicMessage;

/**
 * An error occurred executing a MIFARE Classic command library operation
 */
public class MifareClassicLibraryErrorResponse extends AbstractMifareClassicMessage implements
        StandardErrorResponse {
    public static final byte COMMAND_CODE = 0x7F;
    protected StandardErrorResponseDelegate delegate;

    public interface ErrorCodes {
        /**
         * A command parameter was not valid
         */
        public static final byte INVALID_PARAMETER = 0x01;
        /**
         * A command was received with an insufficient number
         * of parameters
         */
        public static final byte TOO_FEW_PARAMETERS = 0x02;
        /**
         * Too many parameters were specified for command
         */
        public static final byte TOO_MANY_PARAMETERS = 0x03;
        /**
         * A polling error occured. This indicates a rare error mode from
         * the reader itself.
         */
        public static final byte POLLING_ERROR = 0x04;
        /**
         * An error occured while attempting to read the tag
         */
        public static final byte TAG_READ_ERROR = 0x05;
        /**
         * The start block is after the end block
         */
        public static final byte INVALID_BLOCK_ORDER = 0x06;
        /**
         * There was an error attempting to authenticate with the tag
         */
        public static final byte AUTHENTICATION_ERROR = 0x07;
        /**
         * The block number specified is not valid. This occurs when
         * the user specifies a block over 0x3F on a 1K tag
         */
        public static final byte INVALID_BLOCK_NO = 0x08;
        /**
         * The key number specified is not valid
         */
        public static final byte INVALID_KEY_NO = 0x09;
    }

    public MifareClassicLibraryErrorResponse() {
        delegate = new StandardErrorResponseDelegate();
    }

    public MifareClassicLibraryErrorResponse(byte errorCode, byte internalErrorCode, byte readerStatus, String message) {
        delegate = new StandardErrorResponseDelegate(errorCode,internalErrorCode,readerStatus,message);
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
