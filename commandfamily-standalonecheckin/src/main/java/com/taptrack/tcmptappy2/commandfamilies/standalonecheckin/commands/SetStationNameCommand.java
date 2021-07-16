package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.commands;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.standalonecheckin.AbstractStandaloneCheckinMessage;

import java.nio.charset.Charset;

public class SetStationNameCommand extends AbstractStandaloneCheckinMessage {
    public static final byte COMMAND_CODE = 0x04;
    String name;

    public SetStationNameCommand() {
        name = "";
    }

    public SetStationNameCommand(String name) {
        if(name.length() > 16)
            throw new IllegalArgumentException("Name must be at most 16 bytes long");
        this.name = name;
    }

    /**
     * Retrieve the name this command is configured to set the tappy to use
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name this command will set the station to use
     *
     * Note that the name can be at most 16 bytes
     *
     * @param name
     * @throws IllegalArgumentException if the name is over 16 bytes long
     */
    public void setName(String name) {
        if(name.length() > 16)
            throw new IllegalArgumentException("Name must be at most 16 bytes long");
        this.name = name;
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
        if(payload.length > 16)
            throw new MalformedPayloadException("Payload must be at most 16 bytes long");

        name = new String(payload, Charset.forName("UTF-8"));
    }

    @Override
    public byte[] getPayload() {
        return name.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
