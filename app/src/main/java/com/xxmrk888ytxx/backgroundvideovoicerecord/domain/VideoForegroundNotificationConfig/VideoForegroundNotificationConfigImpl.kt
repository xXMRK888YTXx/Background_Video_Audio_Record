package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoForegroundNotificationConfig

import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioForegroundNotificationConfig.AudioForegroundNotificationConfigImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models.ForegroundNotificationConfig
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class VideoForegroundNotificationConfigImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : VideoForegroundNotificationConfig {

    private val videoForegroundNotificationConfigKey = stringPreferencesKey("VideoForegroundNotificationConfigKey")

    override val config: Flow<ForegroundNotificationConfig> = preferencesStorage.getPropertyOrNull(
        videoForegroundNotificationConfigKey
    ).map {
        if(it == null) return@map ForegroundNotificationConfig.ViewRecordStateType

        return@map try {
            Json.decodeFromString(ForegroundNotificationConfig.serializer(),it)
        }catch (e:Exception) {
            Log.e(LOG_TAG,"exception when parse ${e.stackTraceToString()}")
            ForegroundNotificationConfig.ViewRecordStateType
        }
    }

    override suspend fun setConfig(foregroundNotificationConfig: ForegroundNotificationConfig) {
        preferencesStorage.writeProperty(
            videoForegroundNotificationConfigKey,
            Json.encodeToString(
                ForegroundNotificationConfig.serializer(),
                foregroundNotificationConfig
            )
        )
    }

    companion object {
        private const val LOG_TAG = "VideoForegroundNotificationConfigImpl"
    }
}