package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils.MicrochipRtcFormatter;

public class SetTimeAndDateCommand extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x08;
    protected int timestamp;

    public SetTimeAndDateCommand() {
        timestamp = 0;
    }

    public SetTimeAndDateCommand(int unixTimestamp) {
        this.timestamp = unixTimestamp;
    }

    public SetTimeAndDateCommand(byte[] payload) throws MalformedPayloadException {
        parsePayload(payload);
    }

    /**
     * Get the timestamp this command will set the Tappy to use as the current time
     * @return
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp to set the tappy to use as the current time
     *
     * Note that the Tappy's realtime clock only supports the 21st century,
     * so times corresponding to earlier periods of time have undefined behaviour
     *
     * @param timestamp 32-bit unix timestamp
     */
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 7)
            throw new MalformedPayloadException("Payload must be seven bytes");

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
