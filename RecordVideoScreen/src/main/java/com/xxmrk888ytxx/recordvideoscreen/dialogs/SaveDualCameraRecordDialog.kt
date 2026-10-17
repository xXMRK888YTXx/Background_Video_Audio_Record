package com.xxmrk888ytxx.recordvideoscreen.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.recordvideoscreen.R
import com.xxmrk888ytxx.recordvideoscreen.models.DualCameraSaveOption

@Composable
fun SaveDualCameraRecordDialog(
    isProcessing: Boolean,
    onOptionSelected: (DualCameraSaveOption) -> Unit
) {
    Dialog(
        onDismissRequest = { /* Block dismiss until selection is made */ },
        properties = DialogProperties(
            dismissOnBackPress = !isProcessing,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF263346), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📹",
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.save_dual_dialog_title),
                    style = themeTypography.head.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.save_dual_dialog_subtitle),
                    style = themeTypography.body.copy(
                        fontSize = 13.sp,
                        color = themeColors.secondFontColor
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (isProcessing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = themeColors.primaryColor,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.save_dual_processing),
                            style = themeTypography.body.copy(
                                fontSize = 13.sp,
                                color = themeColors.primaryFontColor
                            )
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OptionCard(
                            icon = "📹",
                            title = stringResource(R.string.save_dual_back_only),
                            description = stringResource(R.string.save_dual_back_only_desc),
                            onClick = { onOptionSelected(DualCameraSaveOption.BACK_ONLY) }
                        )

                        OptionCard(
                            icon = "🤳",
                            title = stringResource(R.string.save_dual_front_only),
                            description = stringResource(R.string.save_dual_front_only_desc),
                            onClick = { onOptionSelected(DualCameraSaveOption.FRONT_ONLY) }
                        )

                        OptionCard(
                            icon = "📂",
                            title = stringResource(R.string.save_dual_both_separately),
                            description = stringResource(R.string.save_dual_both_separately_desc),
                            onClick = { onOptionSelected(DualCameraSaveOption.BOTH_SEPARATELY) }
                        )

                        OptionCard(
                            icon = "✨",
                            title = stringResource(R.string.save_dual_both_together),
                            description = stringResource(R.string.save_dual_both_together_desc),
                            isRecommended = true,
                            onClick = { onOptionSelected(DualCameraSaveOption.BOTH_TOGETHER) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    icon: String,
    title: String,
    description: String,
    isRecommended: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = if (isRecommended) themeColors.primaryColor.copy(alpha = 0.6f) else Color(0xFF263346)
    val bgColor = if (isRecommended) Color(0xFF131D31) else Color(0xFF162032)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = themeTypography.body.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
                if (isRecommended) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(themeColors.primaryColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PiP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = themeTypography.body.copy(
                    fontSize = 11.sp,
                    color = themeColors.secondFontColor,
                    lineHeight = 15.sp
                )
            )
        }
    }
}
