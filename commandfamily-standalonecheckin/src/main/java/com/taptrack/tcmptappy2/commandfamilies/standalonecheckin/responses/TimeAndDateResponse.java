package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils.MicrochipRtcFormatter;

public class TimeAndDateResponse extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x0A;
    protected int timestamp;

    public TimeAndDateResponse() {
        timestamp = 0;
    }

    public TimeAndDateResponse(int unixTimestamp) {
        this.timestamp = unixTimestamp;
    }

    /**
     * Retrieve the timestamp corresponding to this time and date
     * @return 32-bit unix timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 7)
            throw new MalformedPayloadException("Payload must be 7 bytes long");

        MicrochipRtcFormatter formatter = new MicrochipRtcFormatter(payload[0],
                payload[1],
                payload[2],
                payload[3],
                payload[4],
                payload[5]);
        timestamp = formatter.getUnixTimestamp();
    }

    @Override
    public byte[] getPayload() {
        MicrochipRtcFormatter formatter = new MicrochipRtcFormatter(timestamp);

        return formatter.getMicrochipRtcFormatted(true,true);
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
