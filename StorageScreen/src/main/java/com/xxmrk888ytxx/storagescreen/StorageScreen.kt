package com.xxmrk888ytxx.storagescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
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

    val pageState = rememberPagerState()
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
            HorizontalPager(state = pageState, pageCount = 2) {
                pages[it].second()
            }
        }
    }
}

@SuppressLint("ResourceType")
@Composable
fun Topbar(storageTypes: ImmutableList<StorageType>, currentPage: Int) {
    BottomNavigation(
        backgroundColor = themeColors.background
    ) {
        storageTypes.forEachIndexed() { index, it ->
            BottomNavigationItem(
                selected = index == currentPage,
                onClick = { it.onClick() },
                selectedContentColor = themeColors.bottomBarSelectedContentColor,
                unselectedContentColor = themeColors.bottomBarUnselectedContentColor,
                label = {
                    Text(
                        text = stringResource(id = it.label),
                        style = themeTypography.bottomBar
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(it.icon),
                        contentDescription = null,
                        modifier = Modifier.size(themeDimensions.iconSize)
                    )
                }
            )
        }
    }
}
