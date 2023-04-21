package com.xxmrk888ytxx.corecompose

import androidx.compose.runtime.staticCompositionLocalOf

val LocalTheme = staticCompositionLocalOf<AppTheme> {
    error("AppTheme not provided")
}