package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase

interface IsCanStartRecordVideoServiceUseCase {
    suspend fun execute() : Boolean
}