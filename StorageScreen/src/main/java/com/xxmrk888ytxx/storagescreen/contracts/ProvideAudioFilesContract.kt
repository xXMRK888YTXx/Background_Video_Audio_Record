package com.xxmrk888ytxx.storagescreen.contracts

import com.xxmrk888ytxx.storagescreen.models.AudioFileModel
import kotlinx.coroutines.flow.Flow

interface ProvideAudioFilesContract {

    val files:Flow<List<AudioFileModel>>
}