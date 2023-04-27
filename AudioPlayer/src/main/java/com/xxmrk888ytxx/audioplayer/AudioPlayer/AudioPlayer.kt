package com.xxmrk888ytxx.audioplayer.AudioPlayer

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AudioPlayer private constructor (
    private val context:Context,
    private val screenLockBlocker: ScreenLockBlocker? = null,
    private val scope:CoroutineScope
) : Player.Listener  {

    @Deprecated("Deprecated in Java")
    @UnstableApi
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_ENDED) {
            stop()
        }
    }

    var isDestroy:Boolean = false
        private set

    private var exoPlayer:ExoPlayer? = null

    private val observeStateScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    private val _currentState:MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.Idle)

    val currentState = _currentState.asStateFlow()


    fun prepare(media:Uri) = scope.launch {
        exoPlayer?.run {
            if(isPlaying) return@launch

            setMediaItem(MediaItem.fromUri(media))

            prepare()
        }


    }

    fun play() = scope.launch {
        exoPlayer?.run {
            if(isPlaying) return@launch

            play()
        }

        toPlayState()
    }

    fun pause() = scope.launch {
        exoPlayer?.run {
            if(!isPlaying) return@launch

            pause()
        }

        toPauseState()

    }

    fun stop() = scope.launch {
        exoPlayer?.run {
            stop()

            clearMediaItems()
        }

        toIdleState()
    }

    fun destroy() = scope.launch {
        observeStateScope.cancel()
        exoPlayer?.run {
            removeListener(this@AudioPlayer)
            stop()
            release()
        }
        screenLockBlocker?.cancel()
        exoPlayer = null
        isDestroy = true
        _currentState.update { PlayerState.Destroy }
        scope.cancel()
    }

    fun seekTo(time:Long) = scope.launch {
        exoPlayer?.run {
            if(!isPlaying) return@launch

            seekTo(time)
        }
    }

    private suspend fun toIdleState() {
        observeStateScope.coroutineContext.cancelChildren()
        _currentState.update { PlayerState.Idle }
        screenLockBlocker?.cancel()
    }

    private suspend fun toPauseState() {
        observeStateScope.coroutineContext.cancelChildren()
        _currentState.update { PlayerState.Pause(it.currentDuration) }
        screenLockBlocker?.cancel()
    }

    private suspend fun toPlayState() {
        _currentState.update { PlayerState.Play(it.currentDuration) }
        screenLockBlocker?.enable()

        observeStateScope.cancelChillersAndLaunch {
            while (true) {
                if(isDestroy || exoPlayer == null) return@cancelChillersAndLaunch
                _currentState.update { PlayerState.Play(exoPlayer!!.currentPosition) }
                delay(900)
            }
        }
    }


    init {
        scope.launch {
            exoPlayer = ExoPlayer.Builder(context).build().apply { addListener(this@AudioPlayer) }
        }
    }


    class Builder(private val context: Context) {
        private var screenLockBlocker: ScreenLockBlocker? = null

        private var scope:CoroutineScope = CoroutineScope(
            SupervisorJob() + Dispatchers.Main
        )

        fun addScreenLockBlocker(screenLockBlocker: ScreenLockBlocker) : Builder {
            this.screenLockBlocker = screenLockBlocker

            return this
        }

        fun setCoroutinesScope(scope: CoroutineScope) : Builder {
            this.scope = scope

            return this
        }

        fun build() : AudioPlayer {
            return AudioPlayer(context, screenLockBlocker,scope)
        }
    }
}