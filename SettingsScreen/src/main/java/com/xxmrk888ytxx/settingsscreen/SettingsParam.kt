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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamShape
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamType

@SuppressLint("ResourceType")
@Composable
internal fun SettingsParam(
    params: SettingsParamType,
    shape: SettingsParamShape,
) {
    val shapeSize = themeDimensions.settingsParamShape
    val cardShape = when (shape) {
        is SettingsParamShape.AllShape -> RoundedCornerShape(shapeSize)
        is SettingsParamShape.TopShape -> RoundedCornerShape(
            topStart = shapeSize,
            topEnd = shapeSize
        )

        is SettingsParamShape.BottomShape -> RoundedCornerShape(
            bottomStart = shapeSize,
            bottomEnd = shapeSize
        )

        is SettingsParamShape.None -> RoundedCornerShape(0.dp)
    }

    val paramsAlpha = if (params.isEnable) 1f else 0.5f

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
        StyleCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = themeDimensions.cardInPaddings,
                    end = themeDimensions.cardOutPaddings
                )
                .heightIn(min = 70.dp)
                .clickable(
                    enabled = params.isEnable,
                    onClick = onClick,
                ),
            shape = cardShape,
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(20.dp))

                    StyleIcon(
                        painter = painterResource(params.icon),
                        tint = themeColors.iconsColor.copy(if (params.isEnable) 1f else 0.5f)
                    )

                    Spacer(modifier = Modifier.width(20.dp))


                    Text(
                        text = params.text,
                        color = themeColors.primaryFontColor.copy(paramsAlpha),
                        modifier = Modifier.widthIn(
                            max = 200.dp
                        ),
                        style = themeTypography.settingsParam
                    )



                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                        when (params) {

                            is SettingsParamType.Switch -> {
                                Switch(
                                    checked = params.isSwitched,
                                    onCheckedChange = params.onStateChanged,
                                    enabled = params.isEnable,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = themeColors.primaryColor,
                                        uncheckedThumbColor = themeColors.uncheckedSettingsSwitch,
                                        uncheckedTrackColor = themeColors.uncheckedSettingsSwitch.copy(
                                            0.5f
                                        ),
                                        disabledCheckedThumbColor = themeColors.primaryColor.copy(
                                            paramsAlpha
                                        ),
                                        disabledCheckedTrackColor = themeColors.primaryColor.copy(
                                            paramsAlpha
                                        ),
                                        disabledUncheckedThumbColor = themeColors.uncheckedSettingsSwitch,
                                        disabledUncheckedTrackColor = themeColors.uncheckedSettingsSwitch.copy(
                                            alpha = 0.5f
                                        )
                                    ),
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                            }

                            is SettingsParamType.Button -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(end = 10.dp)
                                ) {

                                    StyleIcon(
                                        painter = painterResource(R.drawable.arrow),
                                        tint = themeColors.primaryFontColor.copy(0.9f),
                                    )

                                }
                            }

                            is SettingsParamType.Label -> {
                                Text(
                                    text = params.secondaryText,
                                    color = themeColors.primaryFontColor.copy(
                                        if (params.isEnable) 0.6f
                                        else paramsAlpha - 0.2f
                                    ),
                                    modifier = Modifier.padding(end = 10.dp),
                                    style = themeTypography.settingsParam
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
                                            20.sp,
                                            20.sp,
                                            PlaceholderVerticalAlign.TextCenter
                                        )
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_drop_down_triangle),
                                            contentDescription = "",
                                            tint = themeColors.secondFontColor,
                                        )
                                    }
                                )
                                DropdownMenu(
                                    expanded = params.isDropDownVisible,
                                    onDismissRequest = params.onHideDropDown,
                                    modifier = Modifier
                                        .heightIn(max = 200.dp)
                                        .background(themeColors.dropDownColor)
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
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.W600,
                                                color = themeColors.primaryFontColor
                                            )
                                        }

                                    }
                                }

                                Text(
                                    text = annotatedLabelString,
                                    inlineContent = inlineContentMap,
                                    fontWeight = FontWeight.W600,
                                    fontSize = 18.sp,
                                    color = themeColors.secondFontColor,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (!params.isEnable) return@clickable
                                            params.onShowDropDown()
                                        }
                                        .padding(end = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

        }

        if (shape !is SettingsParamShape.BottomShape) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(themeColors.disableColor.copy(0.7f))
                        .height(1.dp)
                )
            }
        }

    }
}