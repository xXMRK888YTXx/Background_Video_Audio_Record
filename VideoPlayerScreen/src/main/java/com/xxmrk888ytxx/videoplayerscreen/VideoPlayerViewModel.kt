package com.xxmrk888ytxx.videoplayerscreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoPlayerViewModel @AssistedInject constructor(
    private val context: Context,
    @Assisted private val videoUri:Uri
) : ViewModel() {

    //player
    private var isPrepared:Boolean = false

    internal val player by lazy {
        ExoPlayer.Builder(context).build()
    }

    internal fun prepare() {
        if(isPrepared) return

        player.setMediaItem(MediaItem.fromUri(videoUri))
        player.prepare()
        isPrepared = true
    }

    internal fun play() {
        player.play()
    }

    override fun onCleared() {
        super.onCleared()
        player.stop()
        player.release()
    }



    @AssistedFactory
    interface Factory {
        fun create(videoUri: Uri) : VideoPlayerViewModel
    }
}