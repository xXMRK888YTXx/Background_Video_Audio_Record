package com.xxmrk888ytxx.videoplayerscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerScreen(
    modifier: Modifier,
    videoPlayerViewModel: VideoPlayerViewModel
) {
    LaunchedEffect(key1 = Unit, block = {
        videoPlayerViewModel.prepare()
        videoPlayerViewModel.play()
    })

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = videoPlayerViewModel.player
            }
        },
        modifier = modifier
    )
}