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
import kotlinx.coroutines.withContext

class AudioPlayer private constructor (
    private val context:Context,
    private val screenLockBlocker: ScreenLockBlocker? = null,
    private val scope:CoroutineScope
)  {

    private val listener by lazy {
        object : Player.Listener {
            @Deprecated("Deprecated in Java")
            @UnstableApi
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    stop()
                }
            }
        }
    }

    /**
     * [Ru]
     * Возвращает true, если данный экземпляр [AudioPlayer] уничтожен.
     * В таком случаи его использование не возможно
     */

    /**
     * [En]
     * Returns true if this instance of [AudioPlayer] is destroyed.
     * In this case it cannot be used
     */
    var isDestroy:Boolean = false
        private set

    /**
     * [Ru]
     * Возвращает true,если аудио фойл подготовлен к произведению, путем вызова [AudioPlayer.prepare].
     *
     * Обратите внимания, что его значение будет false, после вызова [AudioPlayer.destroy]
     */

    /**
     * [En]
     * Returns true if the audio foil is prepared for the work by calling [AudioPlayer.prepare].
     *
     * Note that its value will be false, after calling [AudioPlayer.destroy].
     */
    var isPrepare = false
        private set

    private var exoPlayer:ExoPlayer? = null

    private val observeStateScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    private val _currentState:MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.Idle)

    /**
     * [Ru]
     * Возвращает текущее состояние плеера
     */

    /**
     * [En]
     * Returns the current state of the player
     */
    val currentState = _currentState.asStateFlow()


    /**
     * [Ru]
     * Подготавливает файл к воспроизведению.
     *
     * Данный метод, необходимо вызывать перед вызовом [AudioPlayer.play].
     *
     * Учтите, что после вызова [AudioPlayer.stop], подготовка будет сброшена, и для воспроизведения,
     * необходимо будет заново вызвать данный метод
     */

    /**
     * [En]
     * Prepares the file for playback.
     *
     * This method must be called before calling [AudioPlayer.play].
     *
     * Note that after calling [AudioPlayer.stop], the preparation will be reset,
     * and you will need to call this method again to play,
     * this method will need to be called again
     */
    fun prepare(media:Uri) = scope.launch {
        exoPlayer?.run {
            if(isPlaying) return@launch

            setMediaItem(MediaItem.fromUri(media))

            prepare()

            isPrepare = true
        }


    }

    /**
     * [Ru]
     * Воспроизводит файл, переданный при вызове [AudioPlayer.prepare]
     *
     * Если текущее состояние, это состояние паузы([PlayerState.Pause]),
     * то возобновляет воспроизведение
     *
     * После вызова этого методо, состояние плеера будет [PlayerState.Play]
     */

    /**
     * [En]
     * Plays the file sent when you call [AudioPlayer.prepare]
     *
     * If the current state is the pause state ([PlayerState.Pause]),
     * it resumes playback
     *
     * After calling this method, the player state will be [PlayerState.Play]
     */
    fun play() = scope.launch {
        exoPlayer?.run {
            if(isPlaying) return@launch

            play()
        }

        toPlayState()
    }

    /**
     * [Ru]
     * Приостанавливает воспроизведение,
     * устанавливает состояние плеера [PlayerState.Pause]
     *
     * Воспроизведение будет продолжено, после вызова [AudioPlayer.play]
     */

    /**
     * [En]
     * Pauses playback,
     * Sets the state of the player [PlayerState.Pause]
     *
     * Playback will continue, after calling [AudioPlayer.play]
     */
    fun pause() = scope.launch {
        exoPlayer?.run {
            if(!isPlaying) return@launch

            pause()
        }

        toPauseState()

    }

    /**
     * [Ru]
     * Сбрасывает воспроизведение.
     *
     * Переводит состояние плеера в [PlayerState.Idle].
     * Устанавливает [AudioPlayer.isPrepare] в false, и перед повторным использованием нужно вызвать
     * [AudioPlayer.prepare]
     */

    /**
     * [En]
     * Resets playback.
     *
     * Sets player state to [PlayerState.Idle].
     * Sets [AudioPlayer.isPrepare] to false, and you must call
     * [AudioPlayer.prepare]
     */
    fun stop() = scope.launch {
        exoPlayer?.run {
            stop()

            clearMediaItems()
            isPrepare = false
        }

        toIdleState()
    }

    /**
     * [Ru]
     * Сбрасывает и очищает ресурсы плеера
     *
     * После вызова этого метода, будет не возможно использования плеера
     *
     * Установливает состояние [PlayerState.Destroy]
     */
    fun destroy() = scope.launch {
        observeStateScope.cancel()
        exoPlayer?.run {
            removeListener(listener)
            stop()
            release()
        }
        screenLockBlocker?.cancel()
        exoPlayer = null
        isDestroy = true
        isPrepare = false
        _currentState.update { PlayerState.Destroy }
        scope.cancel()
    }

    /**
     * [Ru]
     * Переводит воспроизведение, на переданую временую точку.
     *
     * Если переданая значение, находится за пределами времени воспроизведения, то будет установлено
     * на последную секунду
     */

    /**
     * [En]
     * Transfers playback, to the transferred time point.
     *
     * If the transmitted value is outside the playback time, it will be set
     * to the last second
     */
    fun seekTo(time:Long) = scope.launch {
        exoPlayer?.run {
            if(!isPlaying) {
                play()
            }

            seekTo(time)
            _currentState.update {
                when(it) {
                    is PlayerState.Play -> PlayerState.Play(time)

                    else -> it
                }
            }
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

                val currentPos = withContext(scope.coroutineContext) {
                    exoPlayer!!.currentPosition
                }

                _currentState.update { PlayerState.Play(currentPos) }
                delay(200)
            }
        }
    }


    init {
        scope.launch {
            exoPlayer = ExoPlayer.Builder(context).build().apply { addListener(listener) }
        }
    }


    class Builder(private val context: Context) {
        private var screenLockBlocker: ScreenLockBlocker? = null

        private var scope:CoroutineScope = CoroutineScope(
            SupervisorJob() + Dispatchers.Main
        )

        /**
         * [Ru]
         * Добавляет [ScreenLockBlocker] в [AudioPlayer].
         *
         * Если [ScreenLockBlocker] добавлен, то он автоматически при воспроизведении вызовет метод
         * [ScreenLockBlocker.enable], а при остановки воспроизведения [ScreenLockBlocker.cancel]
         */

        /**
         * [En]
         * Adds [ScreenLockBlocker] to [AudioPlayer].
         *
         * If [ScreenLockBlocker] is added, it will automatically invoke the
         * [ScreenLockBlocker.enable] method and [ScreenLockBlocker.cancel] method when playback is stopped.
         */
        fun addScreenLockBlocker(screenLockBlocker: ScreenLockBlocker) : Builder {
            this.screenLockBlocker = screenLockBlocker

            return this
        }

        /**
         * [Ru]
         * Задаёт [CoroutineScope] на котором будет выполняться работа с [AudioPlayer].
         * Обратите внимание, что после вызова [AudioPlayer.destroy] переданный [CoroutineScope]
         * будет закрыт.
         */

        /**
         * [En]
         * Sets the [CoroutineScope] on which [AudioPlayer] will be executed.
         * Note that after calling [AudioPlayer.destroy] the passed [CoroutineScope]
         * will be closed.
         */
        fun setCoroutinesScope(scope: CoroutineScope) : Builder {
            this.scope = scope

            return this
        }

        fun build() : AudioPlayer {
            return AudioPlayer(context, screenLockBlocker,scope)
        }
    }
}