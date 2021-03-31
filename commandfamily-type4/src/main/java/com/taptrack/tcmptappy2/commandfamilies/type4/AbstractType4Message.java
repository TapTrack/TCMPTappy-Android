package com.taptrack.tcmptappy2.commandfamilies.type4;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.taptrack.tcmptappy2.AbstractTCMPMessage;

/**
 * Base class for messages within the Type 4 command library
 */
public abstract class AbstractType4Message  extends AbstractTCMPMessage {
    @Override
    @NonNull
    @Size(2)
    public byte[] getCommandFamily() {
        return Type4CommandResolver.FAMILY_ID;
    }
}
