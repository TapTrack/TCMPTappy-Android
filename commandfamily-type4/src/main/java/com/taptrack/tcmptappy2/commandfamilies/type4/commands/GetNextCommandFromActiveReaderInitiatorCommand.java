package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * Command for telling the Tappy to return the next command from an
 * active reader initiator (used in HCE mode)
 *
 * Note: for all uses of timeout, a value of 0 corresponds to infinite polling.
 */
public class GetNextCommandFromActiveReaderInitiatorCommand extends AbstractType4Message {
    public static final byte COMMAND_CODE = (byte) 0x07;

    public GetNextCommandFromActiveReaderInitiatorCommand() {
    }


    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length != 0) {
            throw new MalformedPayloadException("Payload should be empty");
        }
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return new byte[]{};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
