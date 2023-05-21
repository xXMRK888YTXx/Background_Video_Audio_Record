package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen.IgnoreBatteryOptimizationManageContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen.RecordVideoManager
import com.xxmrk888ytxx.recordvideoscreen.contract.IgnoreBatteryOptimizationManageContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoManageContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoStateProviderContract
import dagger.Binds
import dagger.Module

@Module
interface RecordVideoScreenModule {

    @Binds
    fun bindsRecordStateProviderContract(RecordVideoManager: RecordVideoManager): RecordVideoManageContract

    @Binds
    fun bindsRecordVideoStateProviderContract(RecordVideoManager: RecordVideoManager): RecordVideoStateProviderContract

    @Binds
    fun bindsIgnoreBatteryOptimizationManageContract(
        IgnoreBatteryOptimizationManageContractImpl: IgnoreBatteryOptimizationManageContractImpl,
    ): IgnoreBatteryOptimizationManageContract
}