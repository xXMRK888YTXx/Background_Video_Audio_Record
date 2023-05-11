package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList

import android.net.Uri
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.ExportFileUseCase.ExportFileUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ExportAudioContract
import java.io.FileNotFoundException
import javax.inject.Inject

class ExportAudioContractImpl @Inject constructor(
    private val exportFileUseCase: ExportFileUseCase,
    private val audioRecordRepository: AudioRecordRepository
) : ExportAudioContract {

    override suspend fun exportAudio(audioId: Long, outputPath: Uri): Result<Unit> {
        val file = audioRecordRepository.getFileById(audioId.toInt()) ?: return Result.failure(FileNotFoundException())

        return exportFileUseCase.export(file,outputPath)
    }
}