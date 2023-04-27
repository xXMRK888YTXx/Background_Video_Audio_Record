package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.StorageScreen.AudioStorageList

import android.content.Context
import androidx.core.net.toUri
import com.xxmrk888ytxx.audioplayer.AudioPlayer.AudioPlayer
import com.xxmrk888ytxx.audioplayer.AudioPlayer.ScreenLockBlocker
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.PlayerFactoryContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.Player
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class PlayerFactoryContractImpl @Inject constructor(
    private val context: Context,
) : PlayerFactoryContract {

    private class PlayerWrapper(
        context: Context, onLockScreen: () -> Unit, onUnlockScreen: () -> Unit,
    ) : Player {

        private val player: AudioPlayer by lazy {
            AudioPlayer.Builder(context)
                .addScreenLockBlocker(
                    object : ScreenLockBlocker {
                        override fun enable() = onLockScreen()

                        override fun cancel() = onUnlockScreen()
                    }
                ).build()
        }

        override fun prepare(file: File) {
            player.prepare(file.toUri())
        }

        override fun play() {
            player.play()
        }

        override fun pause() {
            player.pause()
        }

        override fun stop() {
            player.stop()
        }

        override fun destroy() {
            player.destroy()
        }

        override fun seekTo(time: Long) {
            player.seekTo(time)
        }

        override val isDestroyed: Boolean
            get() = player.isDestroy

        override val isPrepare: Boolean
            get() = player.isPrepare

        override val state: Flow<PlayerState>
            get() = player.currentState.map {
                when (it) {
                    is com.xxmrk888ytxx.audioplayer.AudioPlayer.PlayerState.Idle -> PlayerState.Idle

                    is com.xxmrk888ytxx.audioplayer.AudioPlayer.PlayerState.Play -> PlayerState.Play(
                        it.currentDuration
                    )

                    is com.xxmrk888ytxx.audioplayer.AudioPlayer.PlayerState.Pause -> PlayerState.Pause(
                        it.currentDuration
                    )

                    is com.xxmrk888ytxx.audioplayer.AudioPlayer.PlayerState.Destroy -> PlayerState.Destroy
                }
            }

    }


    override fun create(onLockScreen: () -> Unit, onUnlockScreen: () -> Unit): Player {
        return PlayerWrapper(context, onLockScreen, onUnlockScreen)
    }

}