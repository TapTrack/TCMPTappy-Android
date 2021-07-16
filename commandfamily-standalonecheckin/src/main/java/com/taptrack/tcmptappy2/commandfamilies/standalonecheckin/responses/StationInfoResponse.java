package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.responses;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.utils.ByteFormatConverters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class StationInfoResponse extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x06;
    protected int id;
    protected String name;

    public StationInfoResponse() {
        id = 0;
        name = "";
    }

    public StationInfoResponse(int id, String name) {
        this.id = id;
        this.name = name;
        if(name.length() > 16) {
            throw new IllegalArgumentException("Name must be less than or equal to 16 characters");
        }
    }

    /**
     * Retrieve the id of the station
     * @return 16-bit unsigned integer value
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Up to 16 byte station name
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name.length() > 16) {
            throw new IllegalArgumentException("Name must be less than or equal to 16 characters");
        }
        this.name = name;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length < 18)
            throw new MalformedPayloadException("Payload is too short");

        id = ByteFormatConverters.bytePairToUnsignedInt16(payload[0],payload[1]);
        byte[] nameSection = Arrays.copyOfRange(payload,2,payload.length);
        if(payload.length > 1) {
            byte[] paddingStripped = ByteFormatConverters.stripPadding(nameSection,(byte)0x00);
            name = new String(paddingStripped,Charset.forName("UTF-8"));
        }
        else {
            name = "";
        }
    }

    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1+name.length());
        try {
            outputStream.write(ByteFormatConverters.unsignedInt16ToByteArray(id));
            byte[] unpaddedNameBytes = name.getBytes(Charset.forName("UTF-8"));
            byte[] paddedNameBytes = ByteFormatConverters.padToLength(unpaddedNameBytes,16,(byte)0x00);
            outputStream.write(paddedNameBytes);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing to ByteArrayOutputStream. This shouldnt be possible.");
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
