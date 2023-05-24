package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IgnoreBatteryOptimizationManager

import kotlinx.coroutines.flow.Flow

interface IgnoreBatteryOptimizationManager {
    
    val isBatteryOptimizationDisabled : Boolean

    val isNeedShowDialogForDisableBatteryOptimization : Flow<Boolean>

    suspend fun hideDisableBatteryDialogForever()

    fun openBatterySettings()
}