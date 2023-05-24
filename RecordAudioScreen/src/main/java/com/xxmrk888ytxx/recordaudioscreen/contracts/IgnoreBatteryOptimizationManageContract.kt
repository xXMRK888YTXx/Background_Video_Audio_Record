package com.xxmrk888ytxx.recordaudioscreen.contracts

import kotlinx.coroutines.flow.Flow

/**
 * [Ru]
 * Интерфейс для упраления игнорированием оптимизации батареи
 */

/**
 * [En]
 * Interface for controlling the ignoring of battery optimization
 */
interface IgnoreBatteryOptimizationManageContract {

    /**
     * [Ru]
     * Возвращает true, если оптимизация батареи для приложения отключена
     */

    /**
     * [En]
     * Returns true if battery optimization is disabled for the application
     */
    val isBatteryOptimizationDisabled : Boolean

    /**
     * [Ru]
     * Возвращает true,если пользователь не отключил показ диалог для придложения отключения оптимизации
     */

    /**
     * [En]
     * Returns true if the user did not turn off the display
     * of the dialog to suggest disabling optimization
     */
    val isNeedShowDialogForDisableBatteryOptimization : Flow<Boolean>

    /**
     * [Ru]
     * При вызове навсегда скрывает диалог который предлагает отключить оптимизацию
     */

    /**
     * [En]
     * When called, it permanently hides the dialog that prompts you to disable optimization
     */
    suspend fun hideDisableBatteryDialogForever()

    /**
     * [Ru]
     * Открывает настройки батареи
     */

    /**
     * [En]
     * Opens the battery settings
     */
    fun openBatterySettings()
}