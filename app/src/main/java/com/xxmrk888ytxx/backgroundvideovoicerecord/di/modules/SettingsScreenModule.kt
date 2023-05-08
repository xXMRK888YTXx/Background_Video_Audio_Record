package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen.ManageCameraConfigContractImpl
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import dagger.Binds
import dagger.Module

@Module
interface SettingsScreenModule {

    @Binds
    fun bindManageCameraConfigContract(
        manageCameraConfigContract: ManageCameraConfigContractImpl
    ): ManageCameraConfigContract
}