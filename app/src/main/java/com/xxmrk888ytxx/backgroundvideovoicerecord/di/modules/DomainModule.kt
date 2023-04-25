package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManagerImpl
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    @AppScope
    fun bindAudioRecordServiceManager(
        audioRecordServiceManager: AudioRecordServiceManagerImpl
    ): AudioRecordServiceManager
}