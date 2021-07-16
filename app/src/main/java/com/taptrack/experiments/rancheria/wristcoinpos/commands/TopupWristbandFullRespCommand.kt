package com.taptrack.experiments.rancheria.wristcoinpos.commands

import com.taptrack.tcmptappy2.MalformedPayloadException

internal class TopupWristbandFullRespCommand : AbstractTopupWristbandCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x06
    }

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super(payload)

    constructor(topupAmountCentavos: Int) : super(topupAmountCentavos)

    constructor(topupAmountCentavos: Int, timeout: Byte) : super(topupAmountCentavos, timeout)

    override fun getCommandCode(): Byte = COMMAND_CODE

}