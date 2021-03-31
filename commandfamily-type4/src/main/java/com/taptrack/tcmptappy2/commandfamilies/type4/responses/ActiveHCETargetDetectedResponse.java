package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * Response when an active reader has initialized the Tappy in HCE mode. It includes the the first valid APDU command recieved by the reader.
   Note: The RATS (Request for Answer to Select) is automatically handled by the Tappy.
 */
public class ActiveHCETargetDetectedResponse extends AbstractType4Message {
    public static final byte COMMAND_CODE = 0x08;

    private byte[] firstCommand;

    public ActiveHCETargetDetectedResponse() {
        firstCommand = new byte[0];
    }

    public ActiveHCETargetDetectedResponse(byte[] firstCommand) {
        this.firstCommand = firstCommand;
    }

    public byte[] getFirstCommand() {
        return firstCommand;
    }

    public void setFirstCommand(byte[] firstCommand) {
        this.firstCommand = firstCommand;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if(payload.length == 0) {
            firstCommand = new byte[0];
        } else {
            firstCommand = payload;
        }


    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return firstCommand;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
