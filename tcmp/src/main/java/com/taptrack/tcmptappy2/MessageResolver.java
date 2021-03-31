package com.taptrack.tcmptappy2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * MessageResolver take in TCMPMessages that may or may not already be resolved to a
 * concrete command attempts to resolve it.
 */
public interface MessageResolver {
    /**
     * Resolves a TCMP message into the explicit message that it represents. Generally used for
     * decoding sent TCMP messages
     * @param message message to decode, must not be null
     * @return TCMPMessage of explicit type or null if the message cannot be matched to a concrete
     * message type
     * @throws MalformedPayloadException Command code valid, but payload is malformed
     */
    @Nullable
    TCMPMessage resolveCommand(@NonNull TCMPMessage message) throws MalformedPayloadException;

    /**
     * Resolves a TCMP message into the explicit message that it represents. Generally used for
     * decoding received TCMP messages
     * @param message message to decode, must not be null
     * @return TCMPMessage of explicit type or null if the message cannot be matched to a concrete
     * message type
     */
    @Nullable
    TCMPMessage resolveResponse(@NonNull TCMPMessage message) throws MalformedPayloadException;
}
