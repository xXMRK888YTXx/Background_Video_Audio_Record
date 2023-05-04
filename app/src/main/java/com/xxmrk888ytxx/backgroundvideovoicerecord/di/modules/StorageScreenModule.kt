package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.ChangeAudioFileNameContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.DeleteAudioFileContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.PlayerFactoryContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.ProvideAudioFilesContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList.ProvideFileByAudioIdImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList.ChangeVideoFileNameContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList.DeleteVideoFileContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList.OpenVideoContactImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.VideoStorageList.ProvideVideoFilesContractImpl
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ChangeAudioFileNameContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.DeleteAudioFileContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.PlayerFactoryContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideAudioFilesContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideFileByAudioId
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ChangeVideoFileNameContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.DeleteVideoFileContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.OpenVideoContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ProvideVideoFilesContract
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
        PlayerFactoryContractImpl: PlayerFactoryContractImpl,
    ): PlayerFactoryContract

    @Binds
    fun bindsProvideFileByAudioId(provideFileByAudioIdImpl: ProvideFileByAudioIdImpl): ProvideFileByAudioId

    @Binds
    fun bindsDeleteAudioFileContract(
        DeleteAudioFileContractImpl: DeleteAudioFileContractImpl,
    ): DeleteAudioFileContract

    @Binds
    fun bindDeleteVideoFileContract(DeleteVideoFileContractImpl: DeleteVideoFileContractImpl): DeleteVideoFileContract

    @Binds
    fun bindProvideVideoFilesContract(ProvideVideoFilesContract: ProvideVideoFilesContractImpl): ProvideVideoFilesContract

    @Binds
    fun bindOpenVideoContract(OpenVideoContract: OpenVideoContactImpl): OpenVideoContract

    @Binds
    fun bindChangeAudioFileNameContract(
        changeVideoFileNameContractImpl: ChangeAudioFileNameContractImpl
    ) : ChangeAudioFileNameContract

    @Binds
    fun bindChangeVideoFileNameContract(
        ChangeVideoFileNameContractImpl: ChangeVideoFileNameContractImpl
    ): ChangeVideoFileNameContract
}