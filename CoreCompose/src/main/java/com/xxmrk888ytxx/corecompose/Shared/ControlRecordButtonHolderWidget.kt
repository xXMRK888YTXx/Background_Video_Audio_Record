package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeShapes

@Composable
fun ControlRecordButtonHolderWidget(
    modifier: Modifier = Modifier,
    content:@Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .then(modifier),
        shape = themeShapes.card,
        backgroundColor = themeColors.cardColor
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
            content = content
        )
    }
}
