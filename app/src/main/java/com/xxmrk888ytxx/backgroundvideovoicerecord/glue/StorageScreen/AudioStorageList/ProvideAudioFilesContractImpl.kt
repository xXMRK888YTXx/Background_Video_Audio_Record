package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideAudioFilesContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.AudioFileModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvideAudioFilesContractImpl @Inject constructor(
    private val audioRecordRepository: AudioRecordRepository
) : ProvideAudioFilesContract {

    override val files: Flow<ImmutableList<AudioFileModel>> = audioRecordRepository.fileList.map { list ->
        list.map { AudioFileModel(it.id,it.duration,it.created,it.name) }.toImmutableList()
    }
}