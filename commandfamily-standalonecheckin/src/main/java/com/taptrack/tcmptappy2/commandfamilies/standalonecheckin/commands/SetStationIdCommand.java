package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils.ByteFormatConverters;

public class SetStationIdCommand extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x03;
    public int stationId;

    public SetStationIdCommand() {
        stationId = 0;
    }

    public SetStationIdCommand(int stationId) {
        this.stationId = stationId;
    }

    /**
     * Get the station's numeric id
     * @return station id, a 16 bit unsigned int
     */
    public int getStationId() {
        return stationId;
    }

    /**
     * Set the numeric id this command will change the station to use
     *
     * This value should be a valid 16 bit unsigned integer. If it is not, the lower
     * two bytes will be used.
     *
     * @param stationId
     */
    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 2)
            throw new MalformedPayloadException("Payload is incorrect length, must contain two bytes");
        stationId = ByteFormatConverters.bytePairToUnsignedInt16(payload[0], payload[1]);
    }

    @Override
    public byte[] getPayload() {
        return ByteFormatConverters.unsignedInt16ToByteArray(stationId);
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
