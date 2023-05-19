package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoForegroundNotificationConfig

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models.ForegroundNotificationConfig
import kotlinx.coroutines.flow.Flow

interface VideoForegroundNotificationConfig {

    val config : Flow<ForegroundNotificationConfig>

    suspend fun setConfig(foregroundNotificationConfig: ForegroundNotificationConfig)
}