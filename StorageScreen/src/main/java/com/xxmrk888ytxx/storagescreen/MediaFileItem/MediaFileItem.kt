package com.xxmrk888ytxx.storagescreen.MediaFileItem

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.coreandroid.toDateString
import com.xxmrk888ytxx.coreandroid.toTimeString
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.Shared.StyleIconButton
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.storagescreen.MediaFileItem.models.MediaFileButton
import com.xxmrk888ytxx.storagescreen.R
import kotlinx.collections.immutable.ImmutableList
import java.io.File

@SuppressLint("ResourceType")
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun LazyItemScope.MediaFileItem(
    duration: Long,
    created:Long,
    name:String,
    buttons:ImmutableList<MediaFileButton>
) {

    val context = LocalContext.current
    StyleCard(
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
            .padding(themeDimensions.cardOutPaddings),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(themeDimensions.cardInPaddings)
        ) {
            if(name.isNotEmpty()) {
                Text(
                    text = name,
                    style = themeTypography.head,
                    color = themeColors.secondFontColor
                )
            }

            Text(
                text = "${created.toDateString(context)}, ${created.toTimeString()}",
                style = themeTypography.head,
                color = themeColors.primaryFontColor
            )

            Text(
                text = duration.milliSecondToString(),
                color = themeColors.secondFontColor,
                style = themeTypography.body
            )

            FlowRow(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                buttons.forEach { button ->
                    StyleIconButton(
                        painter = painterResource(button.icon),
                        onClick = button.onClick
                    )
                }
            }
        }
    }
}