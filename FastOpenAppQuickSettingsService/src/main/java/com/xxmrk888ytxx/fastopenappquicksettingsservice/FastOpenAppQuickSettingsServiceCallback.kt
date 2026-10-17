package com.xxmrk888ytxx.fastopenappquicksettingsservice

import kotlinx.coroutines.flow.Flow

interface FastOpenAppQuickSettingsServiceCallback {
    val isRecordingFlow: Flow<Boolean>
    fun isRecording(): Boolean
    fun getActionTitleRes(): Int
    fun onClicked(titleServiceActivityRunner: TitleServiceActivityRunner)
}