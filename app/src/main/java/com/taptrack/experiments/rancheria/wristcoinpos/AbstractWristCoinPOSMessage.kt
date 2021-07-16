package com.taptrack.experiments.rancheria.wristcoinpos

import com.taptrack.tcmptappy2.AbstractTCMPMessage

internal abstract class AbstractWristCoinPOSMessage : AbstractTCMPMessage() {

    final override fun getCommandFamily(): ByteArray = WristCoinPOSCommandResolver.FAMILY_ID

}
