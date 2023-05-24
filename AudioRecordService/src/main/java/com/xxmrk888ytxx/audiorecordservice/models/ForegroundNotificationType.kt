package com.xxmrk888ytxx.audiorecordservice.models

/**
 * [Ru]
 * Модель класс отвечающая за вид уведомления в время работы foreground сервиса
 *
 * @param isPauseResumeButtonActive - отвечает за то, будет ли показыватся кнопка паузы/воспрозведения записи
 * @param isStopRecordButtonEnabled - отвечает за то, будет ли показыватся кнопка остановки записи
 */

/**
 * [En]
 * Model class responsible for the type of notification during foreground service
 *
 * @param isPauseResumeButtonActive - determines whether the pause/resume button is displayed or not
 * @param isStopRecordButtonEnabled - determines whether the stop record button is displayed or not
 */
sealed class ForegroundNotificationType(
    open val isPauseResumeButtonActive:Boolean,
    open val isStopRecordButtonEnabled:Boolean
) {

    /**
     * [Ru]
     * Модель котороя предствляет собой уведомление с состоянием записи
     */

    /**
     * [En]
     * Model of which is a notification with the state of the record
     */
    data class ViewRecordStateType(
        override val isPauseResumeButtonActive:Boolean,
        override val isStopRecordButtonEnabled:Boolean
    ) : ForegroundNotificationType(isPauseResumeButtonActive, isStopRecordButtonEnabled)

    /**
     * [Ru]
     * Модель котороя предствляет собой уведомление с сообщением которое настроил пользователь
     */

    /**
     * [En]
     * A model that represents a notification with a message that the user has set up
     */
    data class CustomNotification(
        override val isPauseResumeButtonActive:Boolean,
        override val isStopRecordButtonEnabled:Boolean,
        val title:String = "",
        val text:String = ""
    ) : ForegroundNotificationType(isPauseResumeButtonActive, isStopRecordButtonEnabled)
}
