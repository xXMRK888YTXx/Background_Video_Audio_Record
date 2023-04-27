package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.AudioFileModel
import kotlinx.coroutines.flow.Flow

interface ProvideAudioFilesContract {

    val files:Flow<List<AudioFileModel>>
}