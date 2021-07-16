package com.taptrack.experiments.rancheria.wristcoinpos

sealed class AppScratchState {
    object NotScratchable : AppScratchState()
    object Unscratched : AppScratchState()
    object OfflineScratched : AppScratchState()
    object OnlineScratched : AppScratchState()
}

sealed class AppTopupConfigurationSupport {
    object OfflineOnly : AppTopupConfigurationSupport()
    object OnlineOnly : AppTopupConfigurationSupport()
    object DualMode : AppTopupConfigurationSupport()
}

internal data class AppWristbandState internal constructor(
    val uid: ByteArray,
    val majorVersion: Int,
    val minorVersion: Int,
    val offlineCreditTotal: Int?,
    val creditTxCount: Int,
    val debitTotal: Int,
    val debitTxCount: Int,
    val reversalTotal: Int,
    val reversalApprovedCount: Int,
    val reversalDeniedCount: Int,
    val reversalDecisionCount: Int,
    val reversalRequestCount: Int,
    val closeoutCount: Int,
    val aeonCount: Int,
    val deactivatedCount: Int,
    val didReadReversals: Boolean,
    val onlineCreditTotal: Int?,
    val scratchState: AppScratchState,
    val topupConfigurationSupport: AppTopupConfigurationSupport,

    val rewardPointCreditTotal: Int,
    val rewardPointCreditTxCount: Int,
    val rewardPointDebitTotal: Int,
    val rewardPointDebitTxCount: Int,
    val preloadedCreditTotal: Int,
    val preloadedPointsTotal: Int?
) {
    val balance: Int by lazy {
        ((offlineCreditTotal ?: 0) + (onlineCreditTotal ?: 0) + preloadedCreditTotal) + reversalTotal - debitTotal
    }


    val refundableOfflineBalance: Int by lazy {

        if (preloadedCreditTotal == 0) {
            (offlineCreditTotal ?: 0) + reversalTotal - debitTotal
        } else {
            if (debitTotal == 0 && (offlineCreditTotal ?: 0) == 0) {
                0
            } else if ((offlineCreditTotal ?: 0) != 0 && debitTotal > preloadedCreditTotal) {
                if ((offlineCreditTotal ?: 0) - debitTotal + preloadedCreditTotal >= 0) {
                    (offlineCreditTotal ?: 0) - debitTotal + preloadedCreditTotal
                } else {
                    0
                }
            } else if ((offlineCreditTotal ?: 0) != 0 && debitTotal <= preloadedCreditTotal) {
                (offlineCreditTotal ?: 0)
            } else {
                0
            }
        }
    }

    val rewardBalance: Int by lazy {
        rewardPointCreditTotal-rewardPointDebitTotal + (preloadedPointsTotal ?: 0)
    }

    val isClosedOut: Boolean by lazy {
        closeoutCount > aeonCount
    }
    val isDeactivated: Boolean by lazy {
        deactivatedCount > 0
    }

    val isConfiguredForOnlineOperation: Boolean by lazy {
        when (topupConfigurationSupport) {
            AppTopupConfigurationSupport.OfflineOnly -> false
            AppTopupConfigurationSupport.OnlineOnly -> true
            AppTopupConfigurationSupport.DualMode -> when (scratchState) {
                AppScratchState.NotScratchable -> false
                AppScratchState.Unscratched -> false
                AppScratchState.OfflineScratched -> false
                AppScratchState.OnlineScratched -> true
            }
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppWristbandState

        if (!uid.contentEquals(other.uid)) return false
        if (majorVersion != other.majorVersion) return false
        if (minorVersion != other.minorVersion) return false
        if (offlineCreditTotal != other.offlineCreditTotal) return false
        if (creditTxCount != other.creditTxCount) return false
        if (debitTotal != other.debitTotal) return false
        if (debitTxCount != other.debitTxCount) return false
        if (reversalTotal != other.reversalTotal) return false
        if (reversalApprovedCount != other.reversalApprovedCount) return false
        if (reversalDeniedCount != other.reversalDeniedCount) return false
        if (reversalDecisionCount != other.reversalDecisionCount) return false
        if (reversalRequestCount != other.reversalRequestCount) return false
        if (closeoutCount != other.closeoutCount) return false
        if (aeonCount != other.aeonCount) return false
        if (deactivatedCount != other.deactivatedCount) return false
        if (didReadReversals != other.didReadReversals) return false
        if (onlineCreditTotal != other.onlineCreditTotal) return false
        if (scratchState != other.scratchState) return false
        if (topupConfigurationSupport != other.topupConfigurationSupport) return false
        if (rewardPointCreditTotal != other.rewardPointCreditTotal) return false
        if (rewardPointCreditTxCount != other.rewardPointCreditTxCount) return false
        if (rewardPointDebitTotal != other.rewardPointDebitTotal) return false
        if (rewardPointDebitTxCount != other.rewardPointDebitTxCount) return false
        if (preloadedCreditTotal != other.preloadedCreditTotal) return false
        if (preloadedPointsTotal != other.preloadedPointsTotal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.contentHashCode()
        result = 31 * result + majorVersion
        result = 31 * result + minorVersion
        result = 31 * result + (offlineCreditTotal ?: 0)
        result = 31 * result + creditTxCount
        result = 31 * result + debitTotal
        result = 31 * result + debitTxCount
        result = 31 * result + reversalTotal
        result = 31 * result + reversalApprovedCount
        result = 31 * result + reversalDeniedCount
        result = 31 * result + reversalDecisionCount
        result = 31 * result + reversalRequestCount
        result = 31 * result + closeoutCount
        result = 31 * result + aeonCount
        result = 31 * result + deactivatedCount
        result = 31 * result + didReadReversals.hashCode()
        result = 31 * result + (onlineCreditTotal ?: 0)
        result = 31 * result + scratchState.hashCode()
        result = 31 * result + topupConfigurationSupport.hashCode()
        result = 31 * result + rewardPointCreditTotal
        result = 31 * result + rewardPointCreditTxCount
        result = 31 * result + rewardPointDebitTotal
        result = 31 * result + rewardPointDebitTxCount
        result = 31 * result + preloadedCreditTotal
        result = 31 * result + (preloadedPointsTotal ?: 0)
        return result
    }
}
