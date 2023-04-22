package com.xxmrk888ytxx.recordvideoscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.recordvideoscreen.models.RecordWidgetColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class RecordVideoViewModel @Inject constructor() : ViewModel() {

    private val _currentWidgetColor = MutableStateFlow(RecordWidgetColor())

    internal val currentWidgetColor = _currentWidgetColor.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), RecordWidgetColor()
        )

    fun regenerateButtonGradientColor() {
        _currentWidgetColor.update { it.newRecordGradient }
    }

    //Temp
    val isRecord = MutableStateFlow(false)

    fun startRecord() {
        isRecord.update { true }
    }

    fun stopRecord() {
        isRecord.update { false }
    }
}