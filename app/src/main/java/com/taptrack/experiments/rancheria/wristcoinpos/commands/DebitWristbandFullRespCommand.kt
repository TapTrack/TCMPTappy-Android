package com.taptrack.experiments.rancheria.wristcoinpos.commands

import com.taptrack.tcmptappy2.MalformedPayloadException

internal class DebitWristbandFullRespCommand : AbstractDebitWristbandCommand {

    companion object {
        const val COMMAND_CODE: Byte = 0x04
    }

    constructor() : super()

    @Throws(MalformedPayloadException::class)
    constructor(payload: ByteArray) : super(payload)

    constructor(debitAmountCentavos: Int) : super(debitAmountCentavos)

    constructor(debitAmountCentavos: Int, timeout: Byte) : super(debitAmountCentavos, timeout)

    override fun getCommandCode(): Byte = COMMAND_CODE

}
