package com.taptrack.tcmptappy2;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

/**
 * A TCMPMessage represents a command that can be sent to the Tappy
 * or a response that can be recieved from it
 */
public interface TCMPMessage {
    /**
     * Parse a payload into a TCMP message
     * @param payload byte array of the payload
     * @throws MalformedPayloadException the payload's format is not
     * legal for this message
     */
    void parsePayload(@NonNull byte[] payload) throws MalformedPayloadException;

    /**
     * Retrieve a payload that represents the current state of the
     * arguments in this message
     * @return payload byte array
     */
    @NonNull
    byte[] getPayload();

    /**
     * Retrieve the command code associated with this command or response.
     * @return command code byte
     */
    byte getCommandCode();

    /**
     * Retrieve the two-byte command family ID for this TCMP message
     * @return the command family ID
     */
    @NonNull
    @Size(2)
    byte[] getCommandFamily();

    /**
     * Convert this command into a full TCMP packet ready to be sent to the Tappy
     * or as it would be received from the tappy
     * @return byte array of current message
     */
    @NonNull
    byte[] toByteArray();
}
