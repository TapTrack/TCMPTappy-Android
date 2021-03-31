package com.taptrack.tcmptappy2.commandfamilies.basicnfc.responses;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.taptrack.tcmptappy2.MalformedPayloadException;
import com.taptrack.tcmptappy2.commandfamilies.basicnfc.AbstractBasicNfcMessage;

public class EmulationStoppedResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x08;

    private byte stopCode = 0x00;

    @Size(2)
    private final byte[] totalScansBytes = new byte[]{0x00, 0x00};

    public EmulationStoppedResponse() {
    }

    public EmulationStoppedResponse(@NonNull byte[] payload) throws MalformedPayloadException {
        parsePayload(payload);
    }

    @Override
    public void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException {
        if (payload.length < 3) {
            throw new MalformedPayloadException("Payload too short");
        }

        stopCode = payload[0];
        System.arraycopy(payload, 1, totalScansBytes, 0, 2);
    }

    @NonNull
    @Override
    public byte[] getPayload() {
        byte[] payload = new byte[3];
        payload[0] = stopCode;
        System.arraycopy(totalScansBytes, 0, payload, 1, 2);
        return payload;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }

    byte getStopCode() {
        return stopCode;
    }

    @Size(2)
    byte[] getTotalScansBytes() {
        return totalScansBytes;
    }
}
