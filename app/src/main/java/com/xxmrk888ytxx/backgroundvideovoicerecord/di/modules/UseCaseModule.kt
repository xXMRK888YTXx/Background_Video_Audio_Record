package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.ExportFileUseCase.ExportFileUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.ExportFileUseCase.ExportFileUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface UseCaseModule {

    @Binds
    fun bindExportFileUseCase(ExportFileUseCase: ExportFileUseCaseImpl) : ExportFileUseCase
}