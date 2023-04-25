package com.xxmrk888ytxx.corecompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.corecompose.ThemeTypes.Colors
import com.xxmrk888ytxx.corecompose.ThemeTypes.Dimensions
import com.xxmrk888ytxx.corecompose.ThemeTypes.Gradients
import com.xxmrk888ytxx.corecompose.ThemeTypes.Shapes
import com.xxmrk888ytxx.corecompose.ThemeTypes.Typography
import com.xxmrk888ytxx.corecompose.ThemeTypes.Values
import kotlin.random.Random
import kotlin.random.nextInt

val themeColors:Colors
    @Composable get() = LocalTheme.current.colors

val themeDimensions: Dimensions
    @Composable get() = LocalTheme.current.dimensions

val themeGradients: Gradients
    @Composable get() = LocalTheme.current.gradients

val themeShapes: Shapes
    @Composable get() = LocalTheme.current.shapes

val themeTypography: Typography
    @Composable get() = LocalTheme.current.typography

val themeValues: Values
    @Composable get() = LocalTheme.current.values

val randomColor: Color
    get() = Color(
        red = Random(System.currentTimeMillis() / 3).nextInt(0..255),
        green = Random(System.currentTimeMillis() / 2).nextInt(0..255),
        blue = Random(System.currentTimeMillis()).nextInt(0..255)
    )