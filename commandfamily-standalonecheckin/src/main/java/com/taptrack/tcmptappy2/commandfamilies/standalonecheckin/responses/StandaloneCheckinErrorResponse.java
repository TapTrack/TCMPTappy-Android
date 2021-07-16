package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardErrorResponse;
import com.taptrack.tcmptappy2.StandardErrorResponseDelegate;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;

/**
 * An error occurred executing a Standalone Checkin command library operation
 */
public class StandaloneCheckinErrorResponse extends AbstractStandaloneCheckinMessage implements StandardErrorResponse {
    public static final byte COMMAND_CODE = 0x7F;
    protected StandardErrorResponseDelegate delegate;

    public interface ErrorCodes {
        byte TOO_FEW_PARAMETERS = 0x01;
        byte TOO_MANY_PARAMETERS = 0x02;
        /**
         * The tappy can respond with at most 100 checkins at a time
         */
        byte TOO_MANY_CHECKINS_TO_QUERY_AT_ONCE = 0x03;
        /**
         * The tappy cannot use the name specified. This may occur if a
         * name longer than 16 bytes is specified
         */
        byte ERROR_PROGRAMMING_NAME = 0x04;
        /**
         * Can occur when something goes wrong with the realtime clock's oscillator
         */
        byte OSCILLATOR_ERROR = 0x05;
        /**
         * Occurs when there was an error reported when getting or setting the time/date
         */
        byte REALTIME_CLOCK_ERROR = 0x06;
        /**
         * Occurs when there was an unknown  error reported when getting or setting the time/date
         */
        byte UNKNOWN_CLOCK_ERROR = 0x07;
        byte UNRECOGNIZED_COMMAND_CODE = 0x08;
        byte INVALID_PARAMETER = 0x09;
        byte PROBLEM_DETECTING_CARD = 0x0A;
    }

    public StandaloneCheckinErrorResponse() {
        delegate = new StandardErrorResponseDelegate();
    }

    public StandaloneCheckinErrorResponse(byte errorCode, byte internalErrorCode, byte readerStatus, String message) {
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
