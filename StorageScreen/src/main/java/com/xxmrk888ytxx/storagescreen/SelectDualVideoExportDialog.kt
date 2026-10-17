package com.xxmrk888ytxx.storagescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.DualCameraSaveOption
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography

@Composable
fun SelectDualVideoExportDialog(
    primaryCamera: DualCameraPrimary,
    pipPosition: PipPosition,
    onPrimaryChanged: (DualCameraPrimary) -> Unit,
    onPositionChanged: (PipPosition) -> Unit,
    onDismiss: () -> Unit,
    onOptionSelected: (DualCameraSaveOption) -> Unit
) {
    var isSettingsExpanded by rememberSaveable { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF263346), RoundedCornerShape(24.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💾", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.save_dual_dialog_title),
                    style = themeTypography.head.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.save_dual_dialog_subtitle),
                    style = themeTypography.body.copy(
                        fontSize = 12.sp,
                        color = themeColors.secondFontColor
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Options list
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExportOptionCard(
                        icon = "📹",
                        title = stringResource(R.string.save_dual_back_only),
                        description = stringResource(R.string.save_dual_back_only_desc),
                        onClick = { onOptionSelected(DualCameraSaveOption.BACK_ONLY) }
                    )

                    ExportOptionCard(
                        icon = "🤳",
                        title = stringResource(R.string.save_dual_front_only),
                        description = stringResource(R.string.save_dual_front_only_desc),
                        onClick = { onOptionSelected(DualCameraSaveOption.FRONT_ONLY) }
                    )

                    ExportOptionCard(
                        icon = "📂",
                        title = stringResource(R.string.save_dual_both_separately),
                        description = stringResource(R.string.save_dual_both_separately_desc),
                        onClick = { onOptionSelected(DualCameraSaveOption.BOTH_SEPARATELY) }
                    )

                    // Option 4: Combined video with expandable PiP layout
                    ExportCombinedCard(
                        primaryCamera = primaryCamera,
                        pipPosition = pipPosition,
                        isExpanded = isSettingsExpanded,
                        onToggleExpand = { isSettingsExpanded = !isSettingsExpanded },
                        onPrimaryChanged = onPrimaryChanged,
                        onPositionChanged = onPositionChanged,
                        onExportClicked = { onOptionSelected(DualCameraSaveOption.BOTH_TOGETHER) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.Canceled),
                    style = themeTypography.body.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = themeColors.secondFontColor
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ExportCombinedCard(
    primaryCamera: DualCameraPrimary,
    pipPosition: PipPosition,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onPrimaryChanged: (DualCameraPrimary) -> Unit,
    onPositionChanged: (PipPosition) -> Unit,
    onExportClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131D31))
            .border(1.dp, themeColors.primaryColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExportClicked)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✨", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.save_dual_both_together),
                        style = themeTypography.body.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(themeColors.primaryColor)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "PiP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.save_dual_both_together_desc),
                    style = themeTypography.body.copy(
                        fontSize = 11.sp,
                        color = themeColors.secondFontColor,
                        lineHeight = 14.sp
                    )
                )
            }
        }

        // Layout summary and toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${primaryCamera.toDisplayName()} • ${pipPosition.toDisplayName()}",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isExpanded) stringResource(R.string.hide_arrow) else stringResource(R.string.configure_gear),
                    fontSize = 10.sp,
                    color = themeColors.primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                DualCameraLayoutPicker(
                    primaryCamera = primaryCamera,
                    pipPosition = pipPosition,
                    onPrimaryChanged = onPrimaryChanged,
                    onPositionChanged = onPositionChanged
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(themeColors.primaryColor)
                        .clickable(onClick = onExportClicked)
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.export_both_together_btn),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ExportOptionCard(
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF162032))
            .border(1.dp, Color(0xFF263346), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = themeTypography.body.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = themeTypography.body.copy(
                    fontSize = 11.sp,
                    color = themeColors.secondFontColor,
                    lineHeight = 14.sp
                )
            )
        }
    }
}
