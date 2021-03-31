package com.taptrack.tcmptappy2.commandfamilies.ntag21x

import com.taptrack.tcmptappy2.AbstractTCMPMessage

abstract class AbstractNtag21xMessage : AbstractTCMPMessage() {

    override fun getCommandFamily(): ByteArray = Ntag21xCommandResolver.FAMILY_ID

}
