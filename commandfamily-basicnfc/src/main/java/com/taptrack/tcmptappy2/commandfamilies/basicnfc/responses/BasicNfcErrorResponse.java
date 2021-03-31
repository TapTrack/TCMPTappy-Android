/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.StandardErrorResponse;
import com.taptrack.tcmptappy2.StandardErrorResponseDelegate;

;

/**
 * Basic error response for NFC library errors
 */
public class BasicNfcErrorResponse extends AbstractBasicNfcMessage implements
        StandardErrorResponse {
    public static final byte COMMAND_CODE = 0x7F;
    private StandardErrorResponseDelegate delegate;

    public interface ErrorCodes {
        /**
         * A parameter was specific that is not acceptable
         */
        public static final byte INVALID_PARAMETER = 0x01;
        /**
         * Reserved for future use
         */
        public static final byte RFU = 0x02;
        /**
         * A fatal error occurred during polling
         */
        public static final byte POLLING_ERROR = 0x03;
        /**
         * A sufficient number of parameters was not specified
         */
        public static final byte TOO_FEW_PARAMETERS = 0x04;
        /**
         * Attempting to write an NDEF message that is too large for the tag presented
         */
        public static final byte NDEF_MESSAGE_TOO_LARGE = 0x05;
        /**
         * An error occurred creating the NDEF content for writing to the tag
         */
        public static final byte ERROR_CREATING_NDEF_CONTENT = 0x06;
        /**
         * An error occurred while writing NDEF content to the tag
         */
        public static final byte ERROR_WRITING_NDEF_CONTENT = 0x07;
        /**
         * An error occured locking the tag. It may be locked or partially locked to
         * an unrecoverable state.
         */
        public static final byte ERROR_LOCKING_TAG = 0x08;
    }

    public BasicNfcErrorResponse() {
        delegate = new StandardErrorResponseDelegate();
    }

    public BasicNfcErrorResponse(byte errorCode, byte internalError, byte readerStatus, String errorMessage) {
        delegate = new StandardErrorResponseDelegate(errorCode,internalError,readerStatus,errorMessage);
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
