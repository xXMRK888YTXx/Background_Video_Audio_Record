package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoForegroundNotificationConfig.VideoForegroundNotificationConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoForegroundNotificationConfig.VideoForegroundNotificationConfigImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen.ManageAudioNotificationConfigContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen.ManageCameraConfigContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen.ManageVideoNotificationConfigContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen.ProvideAppVersionContractImpl
import com.xxmrk888ytxx.settingsscreen.contracts.ManageAudioNotificationConfigContract
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import com.xxmrk888ytxx.settingsscreen.contracts.ManageVideoNotificationConfigContract
import com.xxmrk888ytxx.settingsscreen.contracts.ProvideAppVersionContract
import dagger.Binds
import dagger.Module

@Module
interface SettingsScreenModule {

    @Binds
    fun bindManageCameraConfigContract(
        manageCameraConfigContract: ManageCameraConfigContractImpl
    ): ManageCameraConfigContract

    @Binds
    fun bindManageAudioNotificationConfigContract(
        ManageAudioNotificationConfigContractImpl: ManageAudioNotificationConfigContractImpl
    ) : ManageAudioNotificationConfigContract

    @Binds
    fun bindVideoForegroundNotificationConfig(
        VideoForegroundNotificationConfigImpl: VideoForegroundNotificationConfigImpl
    ) : VideoForegroundNotificationConfig

    @Binds
    fun bindManageVideoNotificationConfigContract(
        ManageVideoNotificationConfigContractImpl: ManageVideoNotificationConfigContractImpl
    ) : ManageVideoNotificationConfigContract

    @Binds
    fun bindProvideAppVersionContract(
        ProvideAppVersionContractImpl: ProvideAppVersionContractImpl
    ) : ProvideAppVersionContract
}