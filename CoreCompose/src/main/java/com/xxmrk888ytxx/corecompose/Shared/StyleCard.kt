package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeShapes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StyleCard(
    modifier: Modifier = Modifier,
    backgroundColor:Color = themeColors.cardColor,
    shape:Shape = themeShapes.card,
    content:@Composable () -> Unit
) {
    Card(
        modifier = modifier,
        backgroundColor = backgroundColor,
        shape = shape,
        content = content,
    )
}