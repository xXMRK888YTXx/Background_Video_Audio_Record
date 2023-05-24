package com.xxmrk888ytxx.audioplayer.AudioPlayer

/**
 * [Ru]
 * Класс для предстовления состояния плеера
 *
 * @param currentDuration - длительность воспроизведенной аудио записи
 */

/**
 * [En]
 * A class for presenting player status
 *
 * @param currentDuration - duration of played audio record
 */
sealed class PlayerState(open val currentDuration: Long) {

    /**
     * [Ru]
     * Состояния простоя.
     * В этом состоянии находится, до вызова метода [AudioPlayer.play].
     *
     * Возвращается в это состояния, если вызвать метод [AudioPlayer.stop]
     */

    /**
     * [En]
     * Idle states.
     * In this state is, until the [AudioPlayer.play] method is called.
     *
     * Returns to this state if the [AudioPlayer.stop] method is called.
     */
    object Idle : PlayerState(0)

    /**
     * [Ru]
     * Состояние воспроизведения
     *
     * Воспроизводит звук, когда находится в этом состоянии.
     * Переходит в это состояние после вызова метода [AudioPlayer.play]
     */

    /**
     * [En]
     * Playback status
     *
     * Plays the sound when in this state.
     * Moves to this state after calling the [AudioPlayer.play] method.
     */
    data class Play(override val currentDuration: Long) : PlayerState(currentDuration)

    /**
     * [Ru]
     * Состояние паузы
     *
     * Переходит в это состояние, после вызова [AudioPlayer.pause].
     * Воспроизмедение будет преостановлено, до вызова метода [AudioPlayer.play]
     */

    /**
     * [En]
     * Pause state
     *
     * Moves to this state after calling [AudioPlayer.pause].
     * Playback will be paused until the [AudioPlayer.play] method is called.
     */
    data class Pause(override val currentDuration: Long) : PlayerState(currentDuration)

    /**
     * [Ru]
     * Состояние, которое означает что плееп был уничтожен.
     * Если находится в данном состоянии, то использования плеера не возможно
     */

    /**
     * [En]
     * The state which means that the player has been destroyed.
     * If it is in this state, the player cannot be used
     */
    object Destroy : PlayerState(-1)
}

