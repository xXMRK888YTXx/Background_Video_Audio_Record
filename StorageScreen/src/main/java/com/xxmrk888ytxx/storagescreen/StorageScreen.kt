package com.xxmrk888ytxx.storagescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.storagescreen.AudioStorageList.AudioStorageList
import com.xxmrk888ytxx.storagescreen.AudioStorageList.AudioStorageListViewModel
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.StorageType
import com.xxmrk888ytxx.storagescreen.VideoStorageList.VideoStorageList
import com.xxmrk888ytxx.storagescreen.VideoStorageList.VideoStorageListViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("ResourceType")
@Composable
fun StorageScreen(
    audioStorageListViewModel: AudioStorageListViewModel,
    videoStorageListViewModel: VideoStorageListViewModel
) {

    val pageState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val pages: ImmutableList<Pair<StorageType, @Composable () -> Unit>> = remember {
        persistentListOf(
            StorageType(
                label = R.string.Audio,
                icon = R.drawable.music,
                onClick = { scope.launch { pageState.animateScrollToPage(0) } }
            ) to { AudioStorageList(audioStorageListViewModel) },
            StorageType(
                label = R.string.Video,
                icon = R.drawable.video,
                onClick = { scope.launch { pageState.animateScrollToPage(1) } }
            ) to { VideoStorageList(videoStorageListViewModel) },
        )
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            Topbar(pages.map { it.first }.toImmutableList(), pageState.currentPage)
        },
        backgroundColor = Color.Transparent
    ) { paddings ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    top = paddings.calculateTopPadding(),
                    start = paddings.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddings.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = paddings.calculateBottomPadding()
                )
        ) {
            HorizontalPager(state = pageState) {
                pages[it].second()
            }
        }
    }
}

@SuppressLint("ResourceType")
@Composable
fun Topbar(storageTypes: ImmutableList<StorageType>, currentPage: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF111827))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            storageTypes.forEachIndexed { index, it ->
                val isSelected = index == currentPage
                val animatedBg by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF3730A3) else Color.Transparent,
                    animationSpec = tween(220)
                )
                val animatedContentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color(0xFF64748B),
                    animationSpec = tween(220)
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(animatedBg)
                        .clickable { it.onClick() }
                        .padding(horizontal = 22.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(it.icon),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = animatedContentColor
                    )
                    Text(
                        text = stringResource(id = it.label),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = animatedContentColor
                    )
                }
            }
        }
    }
}
