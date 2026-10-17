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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import com.xxmrk888ytxx.corecompose.themeColors

@Composable
fun PipPosition.toDisplayName(): String = when (this) {
    PipPosition.TOP_LEFT -> stringResource(R.string.pip_top_left)
    PipPosition.TOP_RIGHT -> stringResource(R.string.pip_top_right)
    PipPosition.BOTTOM_LEFT -> stringResource(R.string.pip_bottom_left)
    PipPosition.BOTTOM_RIGHT -> stringResource(R.string.pip_bottom_right)
}

@Composable
fun DualCameraPrimary.toDisplayName(): String = when (this) {
    DualCameraPrimary.BACK -> stringResource(R.string.dual_primary_back)
    DualCameraPrimary.FRONT -> stringResource(R.string.dual_primary_front)
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
            text = stringResource(R.string.dual_pip_settings_title),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Main camera
        Text(
            text = stringResource(R.string.primary_camera_fullscreen),
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
                text = stringResource(R.string.camera_back_chip),
                isSelected = isBack,
                modifier = Modifier.weight(1f),
                onClick = { onPrimaryChanged(DualCameraPrimary.BACK) }
            )
            LayoutChip(
                text = stringResource(R.string.camera_selfie_chip),
                isSelected = !isBack,
                modifier = Modifier.weight(1f),
                onClick = { onPrimaryChanged(DualCameraPrimary.FRONT) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // PiP Corner Selection (2x2 grid representing the video screen)
        Text(
            text = stringResource(R.string.pip_window_position),
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
                    text = stringResource(R.string.pip_top_left_arrow),
                    isSelected = pipPosition == PipPosition.TOP_LEFT,
                    modifier = Modifier.weight(1f),
                    onClick = { onPositionChanged(PipPosition.TOP_LEFT) }
                )
                LayoutChip(
                    text = stringResource(R.string.pip_top_right_arrow),
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
                    text = stringResource(R.string.pip_bottom_left_arrow),
                    isSelected = pipPosition == PipPosition.BOTTOM_LEFT,
                    modifier = Modifier.weight(1f),
                    onClick = { onPositionChanged(PipPosition.BOTTOM_LEFT) }
                )
                LayoutChip(
                    text = stringResource(R.string.pip_bottom_right_arrow),
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
