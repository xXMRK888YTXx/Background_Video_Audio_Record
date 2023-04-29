package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen.RecordAudioManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen.RecordVideoManager
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordManageContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordStateProviderContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoManageContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoStateProviderContract
import dagger.Binds
import dagger.Module

@Module
interface RecordVideoScreenModule {

    @Binds
    fun bindsRecordStateProviderContract(RecordVideoManager: RecordVideoManager) : RecordVideoManageContract

    @Binds
    fun bindsRecordVideoStateProviderContract(RecordVideoManager: RecordVideoManager) : RecordVideoStateProviderContract
}