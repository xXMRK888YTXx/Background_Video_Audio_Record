package com.xxmrk888ytxx.audioplayer.AudioPlayer

/**
 * [Ru]
 * Интрефейс для блокировки погашения экрана
 */

/**
 * [En]
 * Interface for locking the screen redemption
 */
interface ScreenLockBlocker {

    fun enable()

    fun cancel()
}