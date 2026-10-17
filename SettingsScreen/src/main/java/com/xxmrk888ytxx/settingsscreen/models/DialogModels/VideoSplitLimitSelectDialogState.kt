package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit

sealed interface VideoSplitLimitSelectDialogState {
    data object Hidden : VideoSplitLimitSelectDialogState
    data class Showed(val currentLimit: VideoSplitLimit) : VideoSplitLimitSelectDialogState
}
