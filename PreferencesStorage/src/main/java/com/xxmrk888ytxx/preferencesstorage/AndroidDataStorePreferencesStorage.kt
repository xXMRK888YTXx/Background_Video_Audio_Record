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
 * Реализация [PreferencesStorage] на основе [DataStore]
 */

/**
 * [En]
 * Implementation of [PreferencesStorage] based on [DataStore]
 */
internal open class AndroidDataStorePreferencesStorage(
    protected val context: Context,
    protected val name:String
) : PreferencesStorage() {

    protected val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = name)


     override suspend fun <TYPE> writeProperty(key: Preferences.Key<TYPE>, value:TYPE) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    override fun <TYPE> getProperty(key: Preferences.Key<TYPE>, defValue:TYPE) : Flow<TYPE> {
        return context.dataStore.data.map {
            it[key] ?: defValue
        }
    }

    override fun <TYPE> getPropertyOrNull(key: Preferences.Key<TYPE>) : Flow<TYPE?> {
        return context.dataStore.data.map {
            it[key]
        }
    }

    override suspend fun <TYPE> removeProperty(key: Preferences.Key<TYPE>) {
        context.dataStore.edit {
            it.remove(key)
        }
    }

    override fun <TYPE> isPropertyExist(key: Preferences.Key<TYPE>) : Flow<Boolean> {
        return context.dataStore.data.map {
            it.contains(key)
        }
    }
}