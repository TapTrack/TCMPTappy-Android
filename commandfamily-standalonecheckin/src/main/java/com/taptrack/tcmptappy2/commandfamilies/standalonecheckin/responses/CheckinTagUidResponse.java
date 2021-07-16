package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;

public class CheckinTagUidResponse extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x05;
    protected byte[] uid;

    public CheckinTagUidResponse() {
        uid = new byte[7];
    }

    public CheckinTagUidResponse(byte[] uid) {
        this.uid = uid;
    }

    /**
     * Get the 7-byte UID for a checkint ag
     * @return
     */
    public byte[] getUid() {
        return uid;
    }

    public void setUid(byte[] uid) {
        if(uid.length != 7) {
            throw new IllegalArgumentException("UID must be 7 bytes long");
        }
        this.uid = uid;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 7)
            throw new MalformedPayloadException("Tag UID incorrect length");

        uid = payload;
    }

    @Override
    public byte[] getPayload() {
        return uid;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
