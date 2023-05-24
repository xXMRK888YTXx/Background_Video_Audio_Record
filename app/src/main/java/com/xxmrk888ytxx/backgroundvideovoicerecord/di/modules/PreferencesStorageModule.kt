package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import android.content.Context
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import dagger.Module
import dagger.Provides

@Module
class PreferencesStorageModule {
    @Provides
    @AppScope
    fun providePreferencesStorage(context: Context) : PreferencesStorage {
        return PreferencesStorage.Factory().create("pref",context)
    }
}