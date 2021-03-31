package com.taptrack.tcmptappy2.commandfamilies.type4.commands;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * Directly transceive an APDU to the card. The APDU should be fully composed
 * by the creator, the Tappy merely send the byte array specified to the tag
 * it is connected to.
 */
public class TransceiveApduCommand extends AbstractType4Message {
    public static final byte COMMAND_CODE = (byte) 0x02;
    private byte[] apdu;

    public TransceiveApduCommand() {
        this.apdu = new byte[0];
    }


    public TransceiveApduCommand(byte[] apdu) {
        this.apdu = apdu;
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        apdu = payload;
    }

    public byte[] getApdu() {
        return apdu;
    }

    public void setApdu(byte[] apdu) {
        this.apdu = apdu;
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        return apdu;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
