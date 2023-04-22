package com.xxmrk888ytxx.recordaudioscreen.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.corecompose.randomColor

@Stable
data class RecordWitgetColor(
    val color1:Color = randomColor
) {
    val newRecordGradient: RecordWitgetColor
        get() = this.copy(
            color1 = randomColor,
        )
}