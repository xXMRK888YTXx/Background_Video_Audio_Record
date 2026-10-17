package com.xxmrk888ytxx.fastopenappquicksettingsservice

interface FastOpenAppQuickSettingsServiceCallback {
    fun isRecording(): Boolean
    fun getActionTitleRes(): Int
    fun onClicked(titleServiceActivityRunner: TitleServiceActivityRunner)
}