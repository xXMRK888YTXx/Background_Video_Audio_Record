package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography

@Composable
fun RecordStateWidget(
    modifier: Modifier = Modifier,
    recordTime:Long = 0,
    isRecordEnabled:Boolean,
    icon:@Composable () -> Unit,
    borderWhenRecordEnabled: Color,
    borderWhenRecordDisabled:Color = themeColors.borderRecordWidgetWhenRecordDisabled,
) {
    Column(
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = recordTime.milliSecondToString(),
                    style = themeTypography.recordCounter,
                    color = themeColors.primaryFontColor,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                icon()
            }
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .then(modifier)
            ) {
                drawCircle(
                    color = if(isRecordEnabled) borderWhenRecordEnabled
                    else borderWhenRecordDisabled,
                    style = Stroke(width = 8.dp.toPx()),
                    center = Offset(x = size.width / 2, y = size.height / 2),
                    radius = 150.dp.toPx()
                )
            }
        }

    }
}