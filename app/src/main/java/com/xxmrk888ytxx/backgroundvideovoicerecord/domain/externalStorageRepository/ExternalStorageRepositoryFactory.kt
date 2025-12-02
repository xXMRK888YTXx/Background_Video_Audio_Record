package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository

import android.content.Context
import androidx.documentfile.provider.DocumentFile

interface ExternalStorageRepositoryFactory {
    fun createFromDocumentFile(rootFolder: DocumentFile,context: Context): ExternalStorageRepository
}