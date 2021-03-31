package com.taptrack.tcmptappy2.commandfamilies.mifareclassic;


import androidx.annotation.NonNull;

import com.taptrack.tcmptappy2.AbstractTCMPMessage;

public abstract class AbstractMifareClassicMessage extends AbstractTCMPMessage {
    @NonNull
    @Override
    public byte[] getCommandFamily() {
        return MifareClassicCommandResolver.FAMILY_ID;
    }
}
