package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase.WriteFileToExternalStorageUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase.WriteFileToExternalStorageUseCaseImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.OpenUrlUseCase.OpenUrlUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.OpenUrlUseCase.OpenUrlUseCaseImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.exportFileUseCase.ExportFileUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.exportFileUseCase.ExportFileUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface UseCaseModule {

    @Binds
    fun bindExportFileUseCase(ExportFileUseCase: WriteFileToExternalStorageUseCaseImpl): WriteFileToExternalStorageUseCase

    @Binds
    fun bindOpenUrlUseCase(openUrlUseCaseImpl: OpenUrlUseCaseImpl): OpenUrlUseCase

    @Binds
    fun bindsExportFileUseCase(exportFileUseCase: ExportFileUseCaseImpl): ExportFileUseCase
}