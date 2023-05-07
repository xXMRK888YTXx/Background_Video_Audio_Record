package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepositoryImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManagerImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManagerImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCaseImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase.IsCanStartRecordVideoServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase.IsCanStartRecordVideoServiceUseCaseImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepositoryImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.ToastManager.ToastManagerImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManagerImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen.ManageCameraTypeContractImpl
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.recordvideoscreen.contract.ManageCameraTypeContract
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    @AppScope
    fun bindAudioRecordServiceManager(
        audioRecordServiceManager: AudioRecordServiceManagerImpl,
    ): AudioRecordServiceManager

    @Binds
    @AppScope
    fun bindAudioRecordRepository(
        AudioRecordRepositoryImpl: AudioRecordRepositoryImpl,
    ): AudioRecordRepository

    @Binds
    @AppScope
    fun bindsVideoRecordServiceManager(
        videoRecordServiceManager: VideoRecordServiceManagerImpl,
    ): VideoRecordServiceManager

    @Binds
    fun bindIsCanStartRecordAudioServiceUseCase(
        IsCanStartRecordAudioServiceUseCaseImpl: IsCanStartRecordAudioServiceUseCaseImpl,
    ): IsCanStartRecordAudioServiceUseCase

    @Binds
    fun bindIsCanStartRecordVideoServiceUseCase(
        IsCanStartRecordVideoServiceUseCaseImpl: IsCanStartRecordVideoServiceUseCaseImpl,
    ): IsCanStartRecordVideoServiceUseCase

    @Binds
    @AppScope
    fun bindVideoRecordRepository(
        VideoRecordRepository: VideoRecordRepositoryImpl,
    ): VideoRecordRepository

    @Binds
    fun bindCameraConfigManager(CameraConfigManager: CameraConfigManagerImpl): CameraConfigManager

    @Binds
    fun bindManageCameraTypeContract(ManageCameraTypeContract: ManageCameraTypeContractImpl): ManageCameraTypeContract

    @Binds
    fun bindToastManager(toastManager: ToastManagerImpl) : ToastManager

}