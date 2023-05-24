package com.xxmrk888ytxx.preferencesstorage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [Ru]
 * Класс для управления пользовательскими предпочтениями
 */

/**
 * [En]
 * Class for managing user preferences
 */
abstract class PreferencesStorage {
    /**
     * [Ru]
     * Записывает значение по ключу
     *
     * @param key - Ключ по которому будет записано заначение
     * @param value - Значение которое будет записано
     */

    /**
     * [En]
     * Writes value by key
     *
     * @param key - The key by which the value will be written
     * @param value - Value to be written
     */
    abstract suspend fun <TYPE> writeProperty(key:Preferences.Key<TYPE>,value:TYPE)

    /**
     * [Ru]
     * Получает значение по ключу
     *
     * @param key - Ключ по которому будет записано заначение
     * @param defValue - Стандартное значение, которое будет возвращено если,
     * значение по ключу не будет найдено
     */

    /**
     * [En]
     * Gets value by key
     *
     * @param key - The key by which the value will be written
     * @param defValue - Default value that will be returned if,
     * key value will not be found
     */
    abstract fun <TYPE> getProperty(key:Preferences.Key<TYPE>,defValue:TYPE) : Flow<TYPE>

    /**
     * [Ru]
     * Возвращает значение по ключу, если значение не будет найдено возвращает null
     *
     * @param key - Ключ по которому будет записано заначение
     */

    /**
     * [En]
     * Returns a value by key, if the value is not found returns null
     *
     * @param key - The key by which the value will be written
     */
    abstract fun <TYPE> getPropertyOrNull(key:Preferences.Key<TYPE>) : Flow<TYPE?>

    /**
     * [Ru]
     * Удаляет значение по ключу.
     *
     * @param key - Ключ, по которому будут удалены данные.
     */

    /**
     * [En]
     * Removes a value by key.
     *
     * @param key - The key by which the data will be deleted.
     */
    abstract suspend fun <TYPE> removeProperty(key:Preferences.Key<TYPE>)

    /**
     * [Ru]
     * Метод для проверки, существуют ли данные по определённому ключу.
     *
     * @param key - ключ для проверки
     */

    /**
     * [En]
     * A method for checking if data exists for a specific key.
     *
     * @param key - key to check
     */
    abstract fun <TYPE> isPropertyExist(key:Preferences.Key<TYPE>) : Flow<Boolean>

    class Factory {
        fun create(fileName:String,context: Context) : PreferencesStorage {
            return AndroidDataStorePreferencesStorage(context,fileName)
        }
    }
}