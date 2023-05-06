package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
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
                background = Color(0xFF1B252D),
                statusBar = Color(0xFF1B252D),
                navigationBar = Color(0xFF1B252D),
                primaryFontColor = Color(0xFFFFFFFF),
                secondFontColor = Color.Gray,
                iconsColor = Color(0xFFFFFFFF),
                bottomBarColor = Color(0xFF1B252D),
                bottomBarSelectedContentColor = Color(0xFF5849C2),
                bottomBarUnselectedContentColor = Color(0x99FFFFFF),
                borderRecordWidgetWhenRecordDisabled = Color(0xFFD40415),
                cardColor = Color(0xFF303F4F),
                recordButtonColor = Color.Red,
                supportControlRecordButtonColor = Color(0xFF5849C2),
                cancelButtonColor = Color(0xFF303F4F).copy(0.7f),
                yesButtonColor = Color(0xFF5849C2)
            )
        override val gradients: Gradients
            get() = Gradients(
                enablePermissionButtonGradient = Brush.linearGradient(listOf(Color(0xFF5849C2),Color(0xFF4871CC))),
                disablePermissionButtonGradient = Brush.linearGradient(listOf(Color(0xFF25313D),Color(0xFF25313D)))
            )
        override val dimensions: Dimensions
            get() = Dimensions(
                iconSize = 30.dp,
                controlButtonSize = 60.dp,
                controlRecordButtonHolderWidgetPadding = 15.dp,
                controlRecordButtonHolderCameraPreviewPadding = 70.dp,
                cardOutPaddings = 10.dp,
                cardInPaddings = 10.dp
            )
        override val shapes: Shapes
            get() = Shapes(
                card = RoundedCornerShape(20.dp),
                controlButtonShape = RoundedCornerShape(100),
                permissionDialog = RoundedCornerShape(15.dp),
                outlineButtonShape = RoundedCornerShape(80)
            )
        override val typography: Typography
            get() = Typography(
                bottomBar = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500
                ),
                recordCounter = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = W900
                ),
                permissionDescription = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = W600
                ),
                head = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = W600
                ),
                body = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = W600
                ),
                playerText = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = W400
                ),
                stub = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = W900
                )
            )
        override val values: Values
            get() = Values(
                animationDuration = 5000,
                additionalAnimationDuration = 100
            )

    }
}