package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import android.content.Context
import com.xxmrk888ytxx.admobmanager.AdMobManager
import dagger.Module
import dagger.Provides

@Module
class AdManagerModule {

    @Provides
    fun provideAdmobManager(context: Context) : AdMobManager {
        return AdMobManager(context)
    }
}