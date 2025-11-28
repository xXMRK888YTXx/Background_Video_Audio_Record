package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository

import androidx.documentfile.provider.DocumentFile

interface ExternalStorageRepository {
    val audioFolder: DocumentFile

    val videoFolder: DocumentFile

    suspend fun createFile(parent: DocumentFile, mineType: String, newFileName: String): Result<DocumentFile>
}