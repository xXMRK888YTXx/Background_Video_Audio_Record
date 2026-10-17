package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamShape
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamType

@SuppressLint("ResourceType")
@Composable
internal fun SettingsParam(
    params: SettingsParamType,
    shape: SettingsParamShape,
) {
    val paramsAlpha = if (params.isEnable) 1f else 0.45f

    val onClick: () -> Unit = when (params) {
        is SettingsParamType.Button -> params.onClick
        is SettingsParamType.Switch -> {
            { params.onStateChanged(!params.isSwitched) }
        }
        is SettingsParamType.DropDown -> params.onShowDropDown
        is SettingsParamType.Label -> {
            {}
        }
    }

    AnimatedVisibility(params.isVisible) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp)
                .clickable(
                    enabled = params.isEnable,
                    onClick = onClick,
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded Icon Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFF1E2838)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(params.icon),
                    contentDescription = null,
                    tint = Color(0xFF818CF8).copy(alpha = paramsAlpha),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = params.text,
                color = Color.White.copy(alpha = paramsAlpha),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Trailing Accessory
            Box(contentAlignment = Alignment.CenterEnd) {
                when (params) {
                    is SettingsParamType.Switch -> {
                        Switch(
                            checked = params.isSwitched,
                            onCheckedChange = params.onStateChanged,
                            enabled = params.isEnable,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF6366F1),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155),
                                disabledCheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                disabledCheckedTrackColor = Color(0xFF6366F1).copy(alpha = 0.5f),
                                disabledUncheckedThumbColor = Color(0xFF64748B),
                                disabledUncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }

                    is SettingsParamType.Button -> {
                        Icon(
                            painter = painterResource(R.drawable.arrow),
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    is SettingsParamType.Label -> {
                        Text(
                            text = params.secondaryText,
                            color = Color(0xFF94A3B8).copy(alpha = paramsAlpha),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    is SettingsParamType.DropDown -> {
                        val annotatedLabelString = buildAnnotatedString {
                            append(params.showSelectedDropDownParam)
                            appendInlineContent("drop_down_triangle")
                        }
                        val inlineContentMap = mapOf(
                            "drop_down_triangle" to InlineTextContent(
                                Placeholder(
                                    18.sp,
                                    18.sp,
                                    PlaceholderVerticalAlign.TextCenter
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_drop_down_triangle),
                                    contentDescription = "",
                                    tint = Color(0xFF818CF8),
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = params.isDropDownVisible,
                            onDismissRequest = params.onHideDropDown,
                            modifier = Modifier
                                .heightIn(max = 200.dp)
                                .background(Color(0xFF1E2838))
                        ) {
                            params.dropDownItems.forEach { item ->
                                DropdownMenuItem(
                                    onClick = {
                                        item.onClick()
                                        if (params.hideDropDownAfterSelect)
                                            params.onHideDropDown()
                                    }
                                ) {
                                    Text(
                                        text = item.text,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Text(
                            text = annotatedLabelString,
                            inlineContent = inlineContentMap,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF818CF8),
                            textAlign = TextAlign.End,
                            modifier = Modifier.clickable(enabled = params.isEnable) {
                                params.onShowDropDown()
                            }
                        )
                    }
                }
            }
        }
    }
}