package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoService.RecordVideoServiceParams
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoService.SaveRecordedVideoStrategyImpl
import com.xxmrk888ytxx.recordvideoservice.RecordVideoParams
import com.xxmrk888ytxx.recordvideoservice.SaveRecordedVideoStrategy
import dagger.Binds
import dagger.Module

@Module
interface RecordVideoServiceModule {

    @Binds
    fun bindRecordVideoParams(recordVideoParams: RecordVideoServiceParams) : RecordVideoParams

    @Binds
    fun bindSaveVideoRecordStrategy(SaveVideoRecordStrategy:SaveRecordedVideoStrategyImpl) : SaveRecordedVideoStrategy
}