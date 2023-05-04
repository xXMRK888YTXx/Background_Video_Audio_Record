package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository.AudioFileNameRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository.AudioFileNameRepositoryImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.VideoFileNameRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.VideoFileNameRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @Binds
    fun bindAudioFileNameRepository(
        audioFileNameRepository: AudioFileNameRepositoryImpl
    ) : AudioFileNameRepository

    @Binds
    fun bindVideoFileNameRepository(
        VideoFileNameRepository:VideoFileNameRepositoryImpl
    ) : VideoFileNameRepository
}