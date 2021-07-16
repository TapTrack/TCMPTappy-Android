package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;

public class ReadCheckinCardUidCommand extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x05;
    private byte timeout;

    public ReadCheckinCardUidCommand() {
        this.timeout = 0x00;
    }

    public ReadCheckinCardUidCommand(byte timeout) {
        this.timeout = timeout;
    }

    /**
     * Retreive the timeout after which the Tappy will stop scanning
     *
     * 0x00 disables timeout
     * @return
     */
    public byte getTimeout() {
        return timeout;
    }

    /**
     * Set the timeout after which the Tappy will stop attempting to read
     *
     * 0x00 disables timeout
     * @param timeout
     */
    public void setTimeout(byte timeout) {
        this.timeout = timeout;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 1)
            throw new MalformedPayloadException("Payload is too short");
        timeout = payload[0];
    }

    @Override
    public byte[] getPayload() {
        return new byte[]{timeout};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
