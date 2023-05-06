package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen.RecordAudioManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen.SetupDelayAudioRecordContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen.SetupDelayVideoRecordContractImpl
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordManageContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordStateProviderContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.SetupDelayAudioRecordContract
import com.xxmrk888ytxx.recordvideoscreen.contract.SetupDelayVideoRecordContract
import dagger.Binds
import dagger.Module

@Module
interface RecordAudioScreenModule {

    @Binds
    fun bindRecordStateProviderContract(
        recordAudioManager: RecordAudioManager
    ) : RecordStateProviderContract

    @Binds
    fun bindRecordManageContract(
        recordAudioManager: RecordAudioManager
    ) : RecordManageContract

    @Binds
    fun bindSetupDelayAudioRecordContract(
        setupDelayVideoRecordContract: SetupDelayAudioRecordContractImpl
    ) : SetupDelayAudioRecordContract
}