package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.audiorecordservice.RecordAudioParams
import com.xxmrk888ytxx.audiorecordservice.SaveAudioRecordStrategy
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AudioRecordService.RecordAudioParamsImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AudioRecordService.SaveAudioRecordStrategyImpl
import dagger.Binds
import dagger.Module

@Module
interface AudioRecordServiceModule {

    @Binds
    fun bindsRecordAudioParams(RecordAudioParamsImpl: RecordAudioParamsImpl) : RecordAudioParams

    @Binds
    fun bindsSaveAudioRecordStrategy(SaveAudioRecordStrategyImpl: SaveAudioRecordStrategyImpl) : SaveAudioRecordStrategy
}