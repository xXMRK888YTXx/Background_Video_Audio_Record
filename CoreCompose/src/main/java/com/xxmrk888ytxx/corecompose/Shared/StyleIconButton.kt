package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions

@Composable
fun StyleIconButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = themeColors.iconsColor,
    onClick:() -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(themeDimensions.iconSize)
        )
    }
}