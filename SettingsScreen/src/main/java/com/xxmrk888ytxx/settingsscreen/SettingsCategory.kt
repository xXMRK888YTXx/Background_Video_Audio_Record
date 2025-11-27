package com.xxmrk888ytxx.settingsscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamShape
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamType
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LazyItemScope.SettingsCategory(
    categoryName: String?,
    settingsParams: ImmutableList<SettingsParamType>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .animateItem()
    ) {

        if (categoryName != null) {
            Text(
                text = categoryName,
                color = themeColors.secondFontColor,
                style = themeTypography.settingCategory
            )

            Spacer(Modifier.height(themeDimensions.paddingBetweenLabelAndSettingsField))
        }

        settingsParams.forEachIndexed { index, param ->
            val shape =
                if (settingsParams.visibleParamsSize == 1) SettingsParamShape.AllShape
                else when (index) {
                    0 -> SettingsParamShape.TopShape
                    settingsParams.visibleParamsLastIndex -> SettingsParamShape.BottomShape
                    else -> SettingsParamShape.None
                }

            SettingsParam(param, shape)

        }

        Spacer(Modifier.height(themeDimensions.categoryPadding))
    }
}

internal inline val List<SettingsParamType>.visibleParamsSize: Int
    get() {
        return this.filter { it.isVisible }.size
    }

internal inline val List<SettingsParamType>.visibleParamsLastIndex: Int
    get() = this.filter { it.isVisible }.lastIndex