package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository

import androidx.documentfile.provider.DocumentFile

interface ExternalStorageRepositoryFactory {
    fun createFromDocumentFile(rootFolder: DocumentFile): ExternalStorageRepository
}