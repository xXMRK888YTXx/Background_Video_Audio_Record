package com.xxmrk888ytxx.audiorecordservice.models

/**
 * [Ru]
 * Параметры для настройки сервиса
 *
 * @param foregroundNotificationType - вид уведомления foreground сервиса
 */

/**
 * [En]
 * Parameters for service setup
 *
 * @param foregroundNotificationType - foreground service notification view
 */
data class RecordAudioConfig(
    val foregroundNotificationType: ForegroundNotificationType
)
