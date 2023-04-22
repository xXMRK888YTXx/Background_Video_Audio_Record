package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W900
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.corecompose.AppTheme
import com.xxmrk888ytxx.corecompose.ThemeTypes.Colors
import com.xxmrk888ytxx.corecompose.ThemeTypes.Dimensions
import com.xxmrk888ytxx.corecompose.ThemeTypes.Shapes
import com.xxmrk888ytxx.corecompose.ThemeTypes.Typography

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
                borderRecordWidgetWhenRecordDisabled = Color(0xFFD40415)
            )
        override val dimensions: Dimensions
            get() = Dimensions(
                iconSize = 25.dp
            )
        override val shapes: Shapes
            get() = Shapes(
                card = RoundedCornerShape(20.dp)
            )
        override val typography: Typography
            get() = Typography(
                bottomBar = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W500
                ),
                recordCounter = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = W900
                )
            )

    }
}