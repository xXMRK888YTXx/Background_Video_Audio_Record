package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import java.io.InputStream

private class ExternalStorageRepositoryImpl constructor(
    private val rootFolder: DocumentFile,
    private val context: Context
) : ExternalStorageRepository {
    override val audioFolder: DocumentFile
        get() = rootFolder.findFile(AUDIO_FOLDER_NAME) ?: createFolder(AUDIO_FOLDER_NAME)

    override val videoFolder: DocumentFile
        get() = rootFolder.findFile(VIDEO_FOLDER_NAME) ?: createFolder(VIDEO_FOLDER_NAME)

    override suspend fun createFile(
        parent: DocumentFile,
        mineType: String,
        newFileName: String
    ): Result<DocumentFile> = runCatching {
        return@runCatching parent.createFile(mineType, newFileName) ?: throw RuntimeException("Impossible create a file in ${parent.name} folder")
    }

    override suspend fun openInputStream(file: DocumentFile): Result<InputStream> = runCatching {
        return@runCatching context.contentResolver.openInputStream(file.uri) ?: throw RuntimeException("Impossible open input stream for ${file.name}")
    }

    private fun createFolder(newFolderName: String): DocumentFile {
        return rootFolder.createDirectory(newFolderName)
            ?: throw IllegalArgumentException("Impossible create new folder with name $newFolderName in write folder")
    }

    companion object {
        private const val AUDIO_FOLDER_NAME = "Audio"
        private const val VIDEO_FOLDER_NAME = "Video"
    }
}

class Factory : ExternalStorageRepositoryFactory {
    override fun createFromDocumentFile(rootFolder: DocumentFile,context: Context): ExternalStorageRepository {
        if (!rootFolder.exists()) throw IllegalArgumentException("rootFolder is not exists")
        return ExternalStorageRepositoryImpl(rootFolder,context)
    }
}