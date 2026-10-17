package com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject

class WriteFileToExternalStorageUseCaseImpl @Inject constructor(
    private val context: Context
) : WriteFileToExternalStorageUseCase {

    override suspend fun write(exportFile: File, outputPath: Uri) : Result<Unit> = withContext(Dispatchers.IO) {
        Log.i(LOG_TAG,"start write")
        var exportFileStream:BufferedInputStream? = null
        var outputFileStream: BufferedOutputStream? = null

        return@withContext try {
            val expectedLength = exportFile.length()
            if (!exportFile.exists() || expectedLength == 0L) {
                throw IOException("Source file does not exist or is empty (0 bytes): ${exportFile.absolutePath}")
            }

            val buffer = ByteArray(65536)
            exportFileStream = BufferedInputStream(FileInputStream(exportFile))
            val rawOutputStream = context.contentResolver.openOutputStream(outputPath)
                ?: throw IOException("Cannot open output stream for $outputPath")
            outputFileStream = BufferedOutputStream(rawOutputStream)

            var totalBytesWritten = 0L
            var readerBytes = exportFileStream.read(buffer)

            while (readerBytes != -1) {
                outputFileStream.write(buffer, 0, readerBytes)
                totalBytesWritten += readerBytes
                readerBytes = exportFileStream.read(buffer)
            }

            outputFileStream.flush()

            if (totalBytesWritten != expectedLength) {
                throw IOException("Incomplete file write: expected $expectedLength bytes, wrote $totalBytesWritten bytes")
            }

            Log.i(LOG_TAG,"write successful ($totalBytesWritten bytes)")
            Result.success(Unit)
        }catch (e:Exception) {
            Log.e(LOG_TAG,"error in write time ${e.stackTraceToString()}")
            try {
                DocumentFile.fromSingleUri(context, outputPath)?.delete()
            } catch (_: Exception) {}
            Result.failure(e)
        } finally {
            withContext(NonCancellable) {
                try { exportFileStream?.close() } catch (_: Exception) {}
                try { outputFileStream?.close() } catch (_: Exception) {}

                exportFileStream = null
                outputFileStream = null
            }
        }
    }

    companion object {
        const val LOG_TAG = "WriteFileToExternalStorageUseCaseImpl"
    }
}