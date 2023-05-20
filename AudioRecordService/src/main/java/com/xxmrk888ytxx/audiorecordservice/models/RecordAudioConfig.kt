package com.xxmrk888ytxx.audiorecordservice.models

/**
 * [Ru]
 * Параметры для настройки сервиса
 *
 * @param tempRecordPath - Директория, в которой времено будет содержаться записываемый файл
 */

/**
 * [En]
 * Parameters for service setup
 *
 * @param tempRecordPath - Directory, which will temporarily contain the recorded file
 */
data class RecordAudioConfig(
    val foregroundNotificationType: ForegroundNotificationType
)
