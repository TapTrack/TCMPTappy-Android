package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils.ByteFormatConverters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GetCheckinsCommand extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x01;
    int firstCheckin;
    int secondCheckin;

    public GetCheckinsCommand() {
        firstCheckin = secondCheckin = 0;
    }

    public GetCheckinsCommand(int firstCheckin, int secondCheckin) {
        this.firstCheckin = firstCheckin;
        this.secondCheckin = secondCheckin;
    }

    public int getFirstCheckin() {
        return firstCheckin;
    }

    public void setFirstCheckin(int firstCheckin) {
        this.firstCheckin = firstCheckin;
    }

    public int getSecondCheckin() {
        return secondCheckin;
    }

    public void setSecondCheckin(int secondCheckin) {
        this.secondCheckin = secondCheckin;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 4)
            throw new MalformedPayloadException("Payload must contain four bytes to be potentially valid");

        firstCheckin = ByteFormatConverters.bytePairToUnsignedInt16(payload[0], payload[1]);
        secondCheckin = ByteFormatConverters.bytePairToUnsignedInt16(payload[2], payload[3]);
    }

    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4);
        try {
            outputStream.write(ByteFormatConverters.unsignedInt16ToByteArray(firstCheckin));
            outputStream.write(ByteFormatConverters.unsignedInt16ToByteArray(secondCheckin));
        } catch (IOException e) {
            throw new IllegalStateException("Error on writing output, this should be impossible",e);
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
