package com.xxmrk888ytxx.recordvideoscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoManageContract
import com.xxmrk888ytxx.recordvideoscreen.contract.RecordVideoStateProviderContract
import com.xxmrk888ytxx.recordvideoscreen.models.RecordState
import com.xxmrk888ytxx.recordvideoscreen.models.RecordWidgetColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class RecordVideoViewModel @Inject constructor(
    private val recordVideoManageContract: RecordVideoManageContract,
    private val recordVideoStateProviderContract: RecordVideoStateProviderContract
) : ViewModel() {

    private val _currentWidgetColor = MutableStateFlow(RecordWidgetColor())

    internal val currentWidgetColor = _currentWidgetColor.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), RecordWidgetColor()
        )

    fun regenerateButtonGradientColor() {
        _currentWidgetColor.update { it.newRecordGradient }
    }

    internal val currentRecordState = recordVideoStateProviderContract.currentState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),RecordState.Idle)

    fun startRecord() {
        recordVideoManageContract.start()
    }

    fun stopRecord() {
        recordVideoManageContract.stop()
    }

    fun pauseRecord() {
        recordVideoManageContract.pause()
    }

    fun resumeRecord() {
        recordVideoManageContract.resume()
    }
}