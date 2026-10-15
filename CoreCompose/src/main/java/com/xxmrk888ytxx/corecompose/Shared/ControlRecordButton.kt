package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeShapes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.ui.Alignment

@Composable
fun ControlRecordButton(
    painter: Painter,
    background: Color,
    onClick:() -> Unit
) {
    Box(
        modifier = Modifier
            .size(themeDimensions.controlButtonSize)
            .clip(themeShapes.controlButtonShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = themeColors.iconsColor,
            modifier = Modifier.size(themeDimensions.iconSize)
        )
    }
}