package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.DeleteAudioFileContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.PlayerFactoryContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.ProvideAudioFilesContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.ProvideFileByAudioIdImpl
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.DeleteAudioFileContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.PlayerFactoryContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideAudioFilesContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideFileByAudioId
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
    fun bindsProvideFileByAudioId(provideFileByAudioIdImpl: ProvideFileByAudioIdImpl) : ProvideFileByAudioId

    @Binds
    fun bindsDeleteAudioFileContract(
        DeleteAudioFileContractImpl: DeleteAudioFileContractImpl
    ) : DeleteAudioFileContract
}