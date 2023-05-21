package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen.IgnoreBatteryOptimizationManageContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordAudioScreen.RecordAudioManager
import com.xxmrk888ytxx.recordaudioscreen.contracts.IgnoreBatteryOptimizationManageContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordManageContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordStateProviderContract
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
    fun bindIgnoreBatteryOptimizationManageContract(
        IgnoreBatteryOptimizationManageContract: IgnoreBatteryOptimizationManageContractImpl
    ) : IgnoreBatteryOptimizationManageContract
}