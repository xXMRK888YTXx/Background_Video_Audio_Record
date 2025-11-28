package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository

import androidx.documentfile.provider.DocumentFile

private class ExternalStorageRepositoryImpl constructor(
    private val rootFolder: DocumentFile
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

    private fun createFolder(newFolderName: String): DocumentFile {
        return rootFolder.createDirectory(newFolderName)
            ?: throw IllegalArgumentException("Impossible create new folder with name $newFolderName in export folder")
    }

    companion object {
        private const val AUDIO_FOLDER_NAME = "Audio"
        private const val VIDEO_FOLDER_NAME = "Video"
    }
}

class Factory : ExternalStorageRepositoryFactory {
    override fun createFromDocumentFile(rootFolder: DocumentFile): ExternalStorageRepository {
        if (!rootFolder.isDirectory) throw IllegalArgumentException("rootFolder must be a directory")
        return ExternalStorageRepositoryImpl(rootFolder)
    }
}