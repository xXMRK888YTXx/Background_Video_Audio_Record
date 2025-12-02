package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.fastOpenAppQuickSettingsService.FastOpenAppQuickSettingsServiceCallbackImpl
import com.xxmrk888ytxx.fastopenappquicksettingsservice.FastOpenAppQuickSettingsServiceCallback
import dagger.Binds
import dagger.Module

@Module
interface FastOpenAppQuickSettingsServiceModule {
    @Binds
    fun bindsFastOpenAppQuickSettingsService(
        fastOpenAppQuickSettingsServiceCallbackImpl: FastOpenAppQuickSettingsServiceCallbackImpl
    ): FastOpenAppQuickSettingsServiceCallback
}