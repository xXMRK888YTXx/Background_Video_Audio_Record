package com.xxmrk888ytxx.corecompose

import com.xxmrk888ytxx.corecompose.ThemeTypes.Colors
import com.xxmrk888ytxx.corecompose.ThemeTypes.Dimensions
import com.xxmrk888ytxx.corecompose.ThemeTypes.Shapes
import com.xxmrk888ytxx.corecompose.ThemeTypes.Typography
import com.xxmrk888ytxx.corecompose.ThemeTypes.Values

interface AppTheme {

    val colors:Colors

    val dimensions:Dimensions

    val shapes:Shapes

    val typography:Typography

    val values: Values
}