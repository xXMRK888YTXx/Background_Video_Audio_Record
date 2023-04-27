package com.xxmrk888ytxx.storagescreen.models

import java.io.File

internal sealed class AudioPlayerDialogState {

    object Hidden : AudioPlayerDialogState()

    data class Showed(
        val player: Player,
        val maxDuration:Long,
        val playFile: File
    ) : AudioPlayerDialogState()
}
