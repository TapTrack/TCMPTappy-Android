package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils.ByteFormatConverters;

public class NumberOfCheckinsResponse extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x02;
    protected int numberOfCheckins;

    public NumberOfCheckinsResponse() {
        numberOfCheckins = 0;
    }

    public NumberOfCheckinsResponse(int numberOfCheckins) {
        this.numberOfCheckins = numberOfCheckins;
    }

    public NumberOfCheckinsResponse(byte[] payload) throws MalformedPayloadException {
        parsePayload(payload);
    }

    /**
     * Represents the number of checkins stored on the Tappy
     *
     * Contains a value that would be a valid 16 bit unsigned int
     * @return
     */
    public int getNumberOfCheckins() {
        return numberOfCheckins;
    }

    public void setNumberOfCheckins(int numberOfCheckins) {
        this.numberOfCheckins = numberOfCheckins;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 4)
            throw new MalformedPayloadException("Payload must be two bytes long");
        numberOfCheckins = ByteFormatConverters.fourByteToUnsignedInt32(payload);
    }

    @Override
    public byte[] getPayload() {
        return ByteFormatConverters.unsignedInt16ToByteArray(numberOfCheckins);
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
