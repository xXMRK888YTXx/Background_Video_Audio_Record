package com.xxmrk888ytxx.videoplayerscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerScreen(
    modifier: Modifier,
    videoPlayerViewModel: VideoPlayerViewModel
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    val observer = remember {
        object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                videoPlayerViewModel.pause()
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                videoPlayerViewModel.play()
            }
        }
    }

    LaunchedEffect(key1 = lifecycleOwner, block = {
        lifecycleOwner.lifecycle.addObserver(observer)
    })

    DisposableEffect(key1 = Unit, effect = {
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    })

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