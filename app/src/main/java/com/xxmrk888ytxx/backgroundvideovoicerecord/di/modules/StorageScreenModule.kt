package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.PlayerFactoryContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.ProvideAudioFilesContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.ProvideFileByAudioIdImpl
import com.xxmrk888ytxx.storagescreen.contracts.PlayerFactoryContract
import com.xxmrk888ytxx.storagescreen.contracts.ProvideAudioFilesContract
import com.xxmrk888ytxx.storagescreen.contracts.ProvideFileByAudioId
import dagger.Binds
import dagger.Module

@Module
interface StorageScreenModule {

    @Binds
    fun bindsProvideAudioFilesContract(
        provideAudioFilesContract: ProvideAudioFilesContractImpl,
    ): ProvideAudioFilesContract

    @Binds
    fun bindsPlayerFactoryContract(
        PlayerFactoryContractImpl: PlayerFactoryContractImpl
    ): PlayerFactoryContract

    @Binds
    fun ProvideFileByAudioId(provideFileByAudioIdImpl: ProvideFileByAudioIdImpl) : ProvideFileByAudioId
}