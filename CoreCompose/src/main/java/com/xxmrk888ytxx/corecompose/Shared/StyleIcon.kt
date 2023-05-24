package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions

@Composable
fun StyleIcon(
    modifier: Modifier = Modifier,
    painter:Painter,
    size:Dp = themeDimensions.iconSize,
    tint: Color = themeColors.iconsColor
) {
    Icon(
        painter = painter,
        contentDescription = "",
        modifier = Modifier
            .size(size)
            .then(modifier),
        tint = tint
    )
}