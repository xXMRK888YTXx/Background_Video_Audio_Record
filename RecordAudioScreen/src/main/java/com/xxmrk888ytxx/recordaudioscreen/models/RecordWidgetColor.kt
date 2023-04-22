package com.xxmrk888ytxx.recordaudioscreen.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.corecompose.randomColor

@Stable
internal data class RecordWidgetColor(
    val color:Color = randomColor
) {
    val newRecordGradient: RecordWidgetColor
        get() = this.copy(
            color = randomColor,
        )
}