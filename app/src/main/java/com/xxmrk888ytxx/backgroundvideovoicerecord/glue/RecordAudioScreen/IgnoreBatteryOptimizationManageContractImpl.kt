package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IgnoreBatteryOptimizationManager.IgnoreBatteryOptimizationManager
import com.xxmrk888ytxx.recordaudioscreen.contracts.IgnoreBatteryOptimizationManageContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IgnoreBatteryOptimizationManageContractImpl @Inject constructor(
    private val batteryOptimizationManager: IgnoreBatteryOptimizationManager,
) : IgnoreBatteryOptimizationManageContract {
    override val isBatteryOptimizationDisabled: Boolean
        get() = batteryOptimizationManager.isBatteryOptimizationDisabled


    override val isNeedShowDialogForDisableBatteryOptimization: Flow<Boolean> =
        batteryOptimizationManager.isNeedShowDialogForDisableBatteryOptimization

    override suspend fun hideDisableBatteryDialogForever() {
        batteryOptimizationManager.hideDisableBatteryDialogForever()
    }

    override fun openBatterySettings() {
        batteryOptimizationManager.openBatterySettings()
    }
}