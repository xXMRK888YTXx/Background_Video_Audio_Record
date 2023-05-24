package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioForegroundNotificationConfig

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models.ForegroundNotificationConfig
import kotlinx.coroutines.flow.Flow


interface AudioForegroundNotificationConfig {

    val config : Flow<ForegroundNotificationConfig>

    suspend fun setConfig(foregroundNotificationConfig:ForegroundNotificationConfig)
}