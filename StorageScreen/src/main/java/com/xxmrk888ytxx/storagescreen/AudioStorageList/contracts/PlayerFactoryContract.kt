package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.Player

/**
 * [Ru]
 * Контракт для создания, экзепляра плеера
 */

/**
 * [En]
 * Contract for the creation, exemplar of the player
 */
interface PlayerFactoryContract {

    /**
     * [Ru]
     * Возвращает экземляр плеера
     *
     * @param onLockScreen - Лямбда для блокировки погашения экрана.
     * @param onUnlockScreen - Лямбда для отмены блокировки погашения экрана.
     */
    /**
     * [En]
     * Returns a copy of the player
     *
     * @param onLockScreen - Lambda for locking screen redemption.
     * @param onUnlockScreen - Lambda for canceling screen redemption lock.
     */
    fun create(onLockScreen:() -> Unit,onUnlockScreen:() -> Unit) : Player
}