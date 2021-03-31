package com.taptrack.tcmptappy2.commandfamilies.type4.responses;

import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.type4.AbstractType4Message;

/**
 * Response corresponding to the tappy successfully transceiving an APDU to
 * a type 4 card. Contains the APDU that the card responded with. Usually will
 * contain at least a status word, but theoretically could be of zero length.
 */
public class APDUTransceiveSuccessfulResponse extends AbstractType4Message {
    public static final byte COMMAND_CODE = 0x02;

    private byte[] apdu;

    public APDUTransceiveSuccessfulResponse() {
        apdu = new byte[0];
    }


    public APDUTransceiveSuccessfulResponse(byte[] apdu) {
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
