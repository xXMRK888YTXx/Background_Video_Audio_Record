package com.xxmrk888ytxx.corecompose

import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.corecompose.ThemeTypes.Colors
import com.xxmrk888ytxx.corecompose.ThemeTypes.Dimensions
import com.xxmrk888ytxx.corecompose.ThemeTypes.Shapes
import com.xxmrk888ytxx.corecompose.ThemeTypes.Typography

val themeColors:Colors
    @Composable get() = LocalTheme.current.colors

val themeDimensions: Dimensions
    @Composable get() = LocalTheme.current.dimensions

val themeShapes: Shapes
    @Composable get() = LocalTheme.current.shapes

val themeTypography: Typography
    @Composable get() = LocalTheme.current.typography