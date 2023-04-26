package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AudioRecordService

import android.content.Context
import com.xxmrk888ytxx.audiorecordservice.RecordAudioParams
import com.xxmrk888ytxx.audiorecordservice.SaveAudioRecordStrategy
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.ApplicationScopeQualifier
import java.io.File
import javax.inject.Inject

class RecordAudioParamsImpl @Inject constructor(
    private val context: Context,
    override val saveAudioRecordStrategy: SaveAudioRecordStrategy
) : RecordAudioParams {

    private val cacheRecordFile by lazy { File(context.cacheDir,"cacheRecordFile") }

    override val recordAudioConfig: RecordAudioConfig = RecordAudioConfig(
        tempRecordPath = cacheRecordFile.absolutePath
    )
}