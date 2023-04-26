package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.ProvideAudioFilesContractImpl
import com.xxmrk888ytxx.storagescreen.contracts.ProvideAudioFilesContract
import dagger.Binds
import dagger.Module

@Module
interface StorageScreenModule {

    @Binds
    fun bindsProvideAudioFilesContract(
        provideAudioFilesContract:ProvideAudioFilesContractImpl
    ) : ProvideAudioFilesContract
}