package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02

import com.taptrack.tcmptappy2.AbstractTCMPMessage


/**
 * Base class for all BasicNfc commands and responses
 */
abstract class AbstractStMicroMessage : AbstractTCMPMessage() {

    override fun getCommandFamily(): ByteArray = STMicroCommandResolver.FAMILY_ID

}