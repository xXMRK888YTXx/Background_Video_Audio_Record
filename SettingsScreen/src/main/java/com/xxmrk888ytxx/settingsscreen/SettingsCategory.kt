package com.xxmrk888ytxx.settingsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamShape
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamType
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LazyItemScope.SettingsCategory(
    categoryName: String?,
    settingsParams: ImmutableList<SettingsParamType>,
) {
    val visibleParams = settingsParams.filter { it.isVisible }
    if (visibleParams.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateItem()
    ) {
        if (categoryName != null) {
            Text(
                text = categoryName.uppercase(),
                color = Color(0xFF818CF8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF131924))
                .border(1.dp, Color(0xFF263346), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                visibleParams.forEachIndexed { index, param ->
                    SettingsParam(
                        params = param,
                        shape = SettingsParamShape.None
                    )
                    if (index < visibleParams.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFF1E2838)
                        )
                    }
                }
            }
        }
    }
}

internal inline val List<SettingsParamType>.visibleParamsSize: Int
    get() = this.filter { it.isVisible }.size

internal inline val List<SettingsParamType>.visibleParamsLastIndex: Int
    get() = this.filter { it.isVisible }.lastIndex