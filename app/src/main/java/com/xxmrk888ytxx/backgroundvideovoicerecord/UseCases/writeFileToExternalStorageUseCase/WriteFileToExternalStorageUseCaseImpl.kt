package com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class WriteFileToExternalStorageUseCaseImpl @Inject constructor(
    private val context: Context
) : WriteFileToExternalStorageUseCase {

    override suspend fun write(exportFile: File, outputPath: Uri) : Result<Unit> = withContext(Dispatchers.IO) {
        Log.i(LOG_TAG,"start write")
        var exportFileStream:BufferedInputStream? = null
        var outputFileStream: BufferedOutputStream? = null

        return@withContext try {
            val buffer = ByteArray(15000)
            exportFileStream = BufferedInputStream(FileInputStream(exportFile))
            outputFileStream = BufferedOutputStream(context.contentResolver.openOutputStream(outputPath))

            var readerBytes = exportFileStream.read(buffer)

            while (readerBytes != -1) {
                outputFileStream.write(buffer)
                readerBytes = exportFileStream.read(buffer)
            }

            Log.i(LOG_TAG,"write successful")
            Result.success(Unit)
        }catch (e:Exception) {
            Log.e(LOG_TAG,"error in write time ${e.stackTraceToString()}")
            Result.failure(e)
        } finally {
            withContext(NonCancellable) {
                exportFileStream?.close()
                outputFileStream?.close()

                exportFileStream = null
                outputFileStream = null
            }
        }
    }

    companion object {
        const val LOG_TAG = "ExportFileUseCaseImpl"
    }
}