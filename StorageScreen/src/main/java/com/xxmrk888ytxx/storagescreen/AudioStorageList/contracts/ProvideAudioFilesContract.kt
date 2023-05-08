package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.AudioFileModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

/**
 * [Ru]
 * Контракт для предоставления записаных аудио записей
 */
/**
 * [En]
 * Contract for the provision of recorded audio recordings
 */
interface ProvideAudioFilesContract {

    val files:Flow<ImmutableList<AudioFileModel>>
}