package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.font.FontWeight.Companion.W900
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.corecompose.AppTheme
import com.xxmrk888ytxx.corecompose.ThemeTypes.Colors
import com.xxmrk888ytxx.corecompose.ThemeTypes.Dimensions
import com.xxmrk888ytxx.corecompose.ThemeTypes.Gradients
import com.xxmrk888ytxx.corecompose.ThemeTypes.Shapes
import com.xxmrk888ytxx.corecompose.ThemeTypes.Typography
import com.xxmrk888ytxx.corecompose.ThemeTypes.Values

@Immutable
object Themes {

    val dark = object : AppTheme {
        override val colors: Colors
            get() = Colors(
                background = Color(0xFF0F1319),
                statusBar = Color(0xFF0F1319),
                navigationBar = Color(0xFF0F1319),
                primaryFontColor = Color(0xFFF8FAFC),
                secondFontColor = Color(0xFF94A3B8),
                iconsColor = Color(0xFFF1F5F9),
                bottomBarColor = Color(0xFF131924),
                bottomBarSelectedContentColor = Color(0xFF818CF8),
                bottomBarUnselectedContentColor = Color(0xFF64748B),
                borderRecordWidgetWhenRecordDisabled = Color(0xFF263346),
                cardColor = Color(0xFF1A2332),
                recordButtonColor = Color(0xFFE11D48),
                supportControlRecordButtonColor = Color(0xFF4F46E5),
                cancelButtonColor = Color(0xFF263346),
                yesButtonColor = Color(0xFF4F46E5),
                primaryColor = Color(0xFF6366F1),
                disableColor = Color(0xFF1E2838),
                uncheckedSettingsSwitch = Color(0xFF475569),
                dropDownColor = Color(0xFF1E2838)
            )
        override val gradients: Gradients
            get() = Gradients(
                enablePermissionButtonGradient = Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF3B82F6))),
                disablePermissionButtonGradient = Brush.linearGradient(listOf(Color(0xFF1E2838), Color(0xFF1E2838)))
            )
        override val dimensions: Dimensions
            get() = Dimensions(
                iconSize = 26.dp,
                controlButtonSize = 64.dp,
                controlRecordButtonHolderWidgetPadding = 18.dp,
                controlRecordButtonHolderCameraPreviewPadding = 70.dp,
                cardOutPaddings = 10.dp,
                cardInPaddings = 14.dp,
                paddingBetweenLabelAndSettingsField = 10.dp,
                categoryPadding = 16.dp,
                settingsParamShape = 14.dp,
            )
        override val shapes: Shapes
            get() = Shapes(
                card = RoundedCornerShape(20.dp),
                controlButtonShape = RoundedCornerShape(100),
                permissionDialog = RoundedCornerShape(20.dp),
                outlineButtonShape = RoundedCornerShape(16.dp)
            )
        override val typography: Typography
            get() = Typography(
                bottomBar = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = W600
                ),
                recordCounter = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = W700
                ),
                permissionDescription = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = W600
                ),
                head = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = W700
                ),
                body = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = W500
                ),
                playerText = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = W400
                ),
                stub = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = W700
                ),
                settingCategory = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = W600
                ),
                settingsParam = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = W600
                ),
                selectDialog = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = W500
                ),
                yesNoDialog = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = W600
                )
            )
        override val values: Values
            get() = Values(
                animationDuration = 2000,
                additionalAnimationDuration = 100
            )

    }
}