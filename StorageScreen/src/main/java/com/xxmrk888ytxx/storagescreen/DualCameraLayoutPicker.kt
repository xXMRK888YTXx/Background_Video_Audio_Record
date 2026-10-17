package com.xxmrk888ytxx.storagescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import com.xxmrk888ytxx.corecompose.themeColors

fun PipPosition.toDisplayName(): String = when (this) {
    PipPosition.TOP_LEFT -> "Вверху слева"
    PipPosition.TOP_RIGHT -> "Вверху справа"
    PipPosition.BOTTOM_LEFT -> "Внизу слева"
    PipPosition.BOTTOM_RIGHT -> "Внизу справа"
}

fun DualCameraPrimary.toDisplayName(): String = when (this) {
    DualCameraPrimary.BACK -> "Задняя"
    DualCameraPrimary.FRONT -> "Селфи"
}

@Composable
fun DualCameraLayoutPicker(
    primaryCamera: DualCameraPrimary,
    pipPosition: PipPosition,
    onPrimaryChanged: (DualCameraPrimary) -> Unit,
    onPositionChanged: (PipPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131D31))
            .border(1.dp, Color(0xFF263346), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Настройки объединения (Dual PiP)",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Main camera
        Text(
            text = "Основная камера (на весь экран):",
            color = themeColors.secondFontColor,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isBack = primaryCamera == DualCameraPrimary.BACK
            LayoutChip(
                text = "📷 Задняя",
                isSelected = isBack,
                modifier = Modifier.weight(1f),
                onClick = { onPrimaryChanged(DualCameraPrimary.BACK) }
            )
            LayoutChip(
                text = "🤳 Селфи",
                isSelected = !isBack,
                modifier = Modifier.weight(1f),
                onClick = { onPrimaryChanged(DualCameraPrimary.FRONT) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // PiP Corner Selection (2x2 grid representing the video screen)
        Text(
            text = "Положение дополнительного окна (PiP):",
            color = themeColors.secondFontColor,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayoutChip(
                    text = "↖ Вверху слева",
                    isSelected = pipPosition == PipPosition.TOP_LEFT,
                    modifier = Modifier.weight(1f),
                    onClick = { onPositionChanged(PipPosition.TOP_LEFT) }
                )
                LayoutChip(
                    text = "↗ Вверху справа",
                    isSelected = pipPosition == PipPosition.TOP_RIGHT,
                    modifier = Modifier.weight(1f),
                    onClick = { onPositionChanged(PipPosition.TOP_RIGHT) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayoutChip(
                    text = "↙ Внизу слева",
                    isSelected = pipPosition == PipPosition.BOTTOM_LEFT,
                    modifier = Modifier.weight(1f),
                    onClick = { onPositionChanged(PipPosition.BOTTOM_LEFT) }
                )
                LayoutChip(
                    text = "↘ Внизу справа",
                    isSelected = pipPosition == PipPosition.BOTTOM_RIGHT,
                    modifier = Modifier.weight(1f),
                    onClick = { onPositionChanged(PipPosition.BOTTOM_RIGHT) }
                )
            }
        }
    }
}

@Composable
private fun LayoutChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) themeColors.primaryColor else Color(0xFF1E293B)
    val borderColor = if (isSelected) themeColors.primaryColor else Color(0xFF334155)
    val textColor = if (isSelected) Color.White else Color(0xFFCBD5E1)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
