package com.xxmrk888ytxx.recordaudioscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordManageContract
import com.xxmrk888ytxx.recordaudioscreen.contracts.RecordStateProviderContract
import com.xxmrk888ytxx.recordaudioscreen.models.RecordState
import com.xxmrk888ytxx.recordaudioscreen.models.RecordWidgetColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class RecordAudioViewModel @Inject constructor(
    private val recordManageContract: RecordManageContract,
    private val recordStateProviderContract: RecordStateProviderContract
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


    internal val recordState: Flow<RecordState> = recordStateProviderContract.currentState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),RecordState.Idle)

    fun startRecord() {
        recordManageContract.start()
    }

    fun stopRecord() {
        recordManageContract.stop()
    }
}