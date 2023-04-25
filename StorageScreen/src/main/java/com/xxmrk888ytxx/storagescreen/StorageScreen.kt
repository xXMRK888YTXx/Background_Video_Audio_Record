package com.xxmrk888ytxx.storagescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.storagescreen.models.StorageType
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("ResourceType")
@Composable
fun StorageScreen(
    storageScreenViewModel: StorageScreenViewModel
) {

    val pageState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val pages:List<Pair<StorageType,@Composable () -> Unit>> = remember {
        listOf(
            StorageType(
                label = R.string.Audio,
                icon = R.drawable.music,
                onClick = { scope.launch { pageState.animateScrollToPage(0) } }
            ) to { AudioStorageList() },
            StorageType(
                label = R.string.Video,
                icon = R.drawable.video,
                onClick = { scope.launch { pageState.animateScrollToPage(1) } }
            ) to { VideoStorageList() },
        )
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            Topbar(pages.map { it.first },pageState.currentPage)
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
            HorizontalPager(state = pageState,pageCount = 2) {
                pages[it].second()
            }
        }
    }
}

@Composable
fun AudioStorageList() { Text(text = "Аудио") }

@Composable
fun VideoStorageList() { Text(text = "Видео") }


@SuppressLint("ResourceType")
@Composable
fun Topbar(storageTypes: List<StorageType>,currentPage:Int) {
    BottomNavigation(
        backgroundColor = themeColors.background
    ) {
        storageTypes.forEachIndexed() { index,it ->
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
                icon = { Icon(
                    painter = painterResource(it.icon),
                    contentDescription = null,
                    modifier = Modifier.size(themeDimensions.iconSize)
                ) }
            )
        }
    }
}
