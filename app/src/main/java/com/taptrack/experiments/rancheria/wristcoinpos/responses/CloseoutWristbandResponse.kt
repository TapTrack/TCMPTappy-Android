package com.taptrack.experiments.rancheria.wristcoinpos.responses

import com.taptrack.experiments.rancheria.wristcoinpos.*
import com.taptrack.kotlin_tlv.TLV
import com.taptrack.kotlin_tlv.lookUpTlvInList
import com.taptrack.kotlin_tlv.lookUpTlvInListIfPresent
import com.taptrack.kotlin_tlv.parseTlvData
import com.taptrack.tcmptappy2.TCMPMessageParseException

internal class CloseoutWristbandResponse : AbstractWristCoinPOSMessage{
    companion object {
        const val COMMAND_CODE: Byte = 0x07
    }

    private lateinit var wristbandState: AppWristbandState

    var _wristbandStatusTlv: ByteArray = byteArrayOf()

    var wristbandStatusTlv: ByteArray
        get() = _wristbandStatusTlv
        private set(value) {
            _wristbandStatusTlv = value
        }

    fun getWristbandState(): AppWristbandState = wristbandState

    constructor() : super()

    @Throws(TCMPMessageParseException::class)
    constructor(payload: ByteArray) : this() {
        parsePayload(payload)
    }

    @Throws(
        TCMPMessageParseException::class, MissingWristbandStateFieldException::class,
        TLV.MalformedTlvByteArrayException::class, InvalidScratchStatusException::class, InvalidTopupConfigurationException::class)
    override fun parsePayload(payload: ByteArray) {
        if (payload.size < 3) {
            throw TCMPMessageParseException("Payload too short")
        }
        var tlvs: List<TLV> = parseTlvData(payload)

        wristbandStatusTlv = payload
        try {
            wristbandState = AppWristbandState(
                uid = lookUpTlvInList(tlvs, type_card_tag_code).value,
                majorVersion = lookUpTlvInList(tlvs, type_card_version).value[0].toInt(),
                minorVersion = lookUpTlvInList(tlvs, type_card_version).value[1].toInt(),
                offlineCreditTotal = lookUpTlvInListIfPresent(tlvs, type_offline_topup_amount)?.value?.toInt(),
                creditTxCount = lookUpTlvInList(tlvs, type_card_credit_tx_count).value.toInt(),
                debitTotal = lookUpTlvInList(tlvs, type_card_debit_total).value.toInt(),
                debitTxCount = lookUpTlvInList(tlvs, type_card_debit_tx_count).value.toInt(),
                reversalTotal = lookUpTlvInList(tlvs, type_card_reversal_total).value.toInt(),
                reversalApprovedCount = lookUpTlvInList(tlvs, type_card_reversal_approved_count).value.toInt(),
                reversalDeniedCount = lookUpTlvInList(tlvs, type_card_reversal_denied_count).value.toInt(),
                reversalDecisionCount = lookUpTlvInList(tlvs, type_card_reversal_decision_count).value.toInt(),
                reversalRequestCount = lookUpTlvInList(tlvs, type_reversal_request_count).value.toInt(),
                closeoutCount = lookUpTlvInList(tlvs, type_card_closeout_count).value.toInt(),
                aeonCount = lookUpTlvInList(tlvs, type_card_aeon_count).value.toInt(),
                deactivatedCount = lookUpTlvInList(tlvs, type_deactivate_count).value.toInt(),
                scratchState = when (lookUpTlvInList(tlvs, type_scratch_status).value[0]) {
                    0x00.toByte() -> AppScratchState.NotScratchable
                    0x01.toByte() -> AppScratchState.Unscratched
                    0x02.toByte() -> AppScratchState.OfflineScratched
                    0x03.toByte() -> AppScratchState.OnlineScratched
                    else -> throw InvalidScratchStatusException()
                },
                topupConfigurationSupport = when (lookUpTlvInList(tlvs, type_topup_mode_support).value[0]) {
                    0x00.toByte() -> AppTopupConfigurationSupport.OfflineOnly
                    0x01.toByte() -> AppTopupConfigurationSupport.OnlineOnly
                    0x02.toByte() -> AppTopupConfigurationSupport.DualMode
                    else -> throw InvalidTopupConfigurationException()
                },
                preloadedCreditTotal = lookUpTlvInList(tlvs, type_preloaded_credit_amount).value.toInt(),
                didReadReversals = false,
                onlineCreditTotal = lookUpTlvInListIfPresent(tlvs, type_online_topup_amount)?.value?.toInt(),
                rewardPointCreditTotal = lookUpTlvInList(tlvs, type_reward_credit_total).value.toInt(),
                rewardPointCreditTxCount = lookUpTlvInList(tlvs, type_reward_credit_tx_count).value.toInt(),
                rewardPointDebitTotal = lookUpTlvInList(tlvs, type_reward_debit_total).value.toInt(),
                rewardPointDebitTxCount = lookUpTlvInList(tlvs, type_reward_debit_tx_count).value.toInt(),
                preloadedPointsTotal = lookUpTlvInListIfPresent(tlvs, type_preloaded_points_amount)?.value?.toInt(),
            )
        } catch (e: TLV.TLVNotFoundException) {
            throw MissingWristbandStateFieldException()
        }
    }

    override fun getPayload(): ByteArray = wristbandStatusTlv

    override fun getCommandCode(): Byte = COMMAND_CODE
}