package com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.commands

import com.taptrack.tcmptappy2.commandfamilies.stmicroM24SR02.AbstractStMicroMessage

class GetI2CSettingCommand : AbstractStMicroMessage {
    var timeout: Byte = 0

    companion object {
        const val COMMAND_CODE: Byte = 0x07
    }

    constructor() : super()

    @Throws(IllegalArgumentException::class)
    constructor(timeout: Byte) : super() {
       this.timeout = timeout
    }

    override fun parsePayload(payload: ByteArray){
        this.timeout = payload[0]
    }

    override fun getPayload(): ByteArray = byteArrayOf(timeout)

    override fun getCommandCode(): Byte = COMMAND_CODE

}