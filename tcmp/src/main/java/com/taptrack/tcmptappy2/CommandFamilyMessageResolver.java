package com.taptrack.tcmptappy2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

/**
 * CommandFamilyParsers are used to parse commands
 * for a specific family of TCMP commands/responses.
 */
public interface CommandFamilyMessageResolver extends MessageResolver {
    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the message passed does not correspond
     * to this command family
     */
    @Nullable
    @Override
    TCMPMessage resolveCommand(@NonNull TCMPMessage message) throws MalformedPayloadException;

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the message passed does not correspond
     * to this command family
     */
    @Nullable
    @Override
    TCMPMessage resolveResponse(@NonNull TCMPMessage message) throws MalformedPayloadException;

    /**
     * Get the two-byte command family identifier for this command family
     * @return byte array of length two representing the command family id
     */
    @NonNull
    @Size(2)
    byte[] getCommandFamilyId();
}
