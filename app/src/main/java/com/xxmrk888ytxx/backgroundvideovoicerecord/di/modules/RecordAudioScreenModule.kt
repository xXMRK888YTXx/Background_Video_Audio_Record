package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen.RecordManager
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordManageContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordStateProviderContract
import dagger.Binds
import dagger.Module

@Module
interface RecordAudioScreenModule {

    @Binds
    fun bindRecordStateProviderContract(
        recordManager: RecordManager
    ) : RecordStateProviderContract

    @Binds
    fun bindRecordManageContract(
        recordManager: RecordManager
    ) : RecordManageContract
}