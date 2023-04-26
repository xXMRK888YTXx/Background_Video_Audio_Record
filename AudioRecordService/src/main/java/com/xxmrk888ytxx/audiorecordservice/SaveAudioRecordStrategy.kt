package com.xxmrk888ytxx.audiorecordservice

import kotlinx.coroutines.CoroutineScope
import java.io.File

interface SaveAudioRecordStrategy {

    val scope: CoroutineScope

    suspend fun saveRecord(recordedFile: File)
}