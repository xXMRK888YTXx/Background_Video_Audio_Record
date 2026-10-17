package com.xxmrk888ytxx.bottombarscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.bottombarscreen.models.BottomBarScreenModel
import com.xxmrk888ytxx.corecompose.LocalCurrentBottomBarPage
import com.xxmrk888ytxx.corecompose.LocalIsPageActive
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomBarScreen(
    bottomBarScreens: ImmutableList<BottomBarScreenModel>,
) {
    val pager = rememberPagerState { bottomBarScreens.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        backgroundColor = Color.Transparent,
        bottomBar = {
            BottomBar(
                bottomBarScreens = bottomBarScreens,
                currentPage = pager.currentPage,
                onScrollPage = {
                    scope.launch { pager.animateScrollToPage(it) }
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            state = pager
        ) { screen ->
            androidx.compose.runtime.CompositionLocalProvider(
                LocalCurrentBottomBarPage provides pager.currentPage,
                LocalIsPageActive provides (pager.currentPage == screen)
            ) {
                bottomBarScreens[screen].content()
            }
        }
    }
}

@SuppressLint("ResourceType")
@Composable
fun BottomBar(
    bottomBarScreens: List<BottomBarScreenModel>,
    currentPage: Int,
    onScrollPage: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        NavigationBar(
            containerColor = Color(0xFF111827),
            contentColor = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            bottomBarScreens.forEachIndexed { index, bottomBarScreenModel ->
                val isSelected = index == currentPage
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onScrollPage(index) },
                    icon = {
                        Icon(
                            painter = painterResource(bottomBarScreenModel.icon),
                            contentDescription = bottomBarScreenModel.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = bottomBarScreenModel.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color(0xFF818CF8),
                        indicatorColor = Color(0xFF3730A3),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )
            }
        }
    }
}
