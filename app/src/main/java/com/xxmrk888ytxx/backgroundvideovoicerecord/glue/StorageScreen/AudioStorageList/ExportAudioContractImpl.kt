package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList

import android.net.Uri
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase.WriteFileToExternalStorageUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ExportAudioContract
import java.io.FileNotFoundException
import javax.inject.Inject

class ExportAudioContractImpl @Inject constructor(
    private val writeFileToExternalStorageUseCase: WriteFileToExternalStorageUseCase,
    private val audioRecordRepository: AudioRecordRepository
) : ExportAudioContract {

    override suspend fun exportAudio(audioId: Long, outputPath: Uri): Result<Unit> {
        val file = audioRecordRepository.getFileById(audioId) ?: return Result.failure(FileNotFoundException())

        return writeFileToExternalStorageUseCase.write(file,outputPath)
    }
}