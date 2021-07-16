package com.taptrack.tcmptappy2.commandfamilies.standalonecheckin;

import com.taptrack.tcmptappy2.AbstractTCMPMessage;

public abstract class AbstractStandaloneCheckinMessage extends AbstractTCMPMessage {
    @Override
    public byte[] getCommandFamily() {
        return StandaloneCheckinCommandResolver.FAMILY_ID;
    }
}
