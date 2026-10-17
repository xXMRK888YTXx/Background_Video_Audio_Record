package com.xxmrk888ytxx.fastopenappquicksettingsservice

interface FastOpenAppQuickSettingsServiceCallback {
    fun isRecording(): Boolean
    fun onClicked(titleServiceActivityRunner: TitleServiceActivityRunner)
}