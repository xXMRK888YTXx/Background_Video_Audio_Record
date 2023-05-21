package com.xxmrk888ytxx.recordaudioscreen.contracts

import kotlinx.coroutines.flow.Flow

interface IgnoreBatteryOptimizationManageContract {

    val isBatteryOptimizationDisabled : Boolean

    val isNeedShowDialogForDisableBatteryOptimization : Flow<Boolean>

    suspend fun hideDisableBatteryDialogForever()

    fun openBatterySettings()
}