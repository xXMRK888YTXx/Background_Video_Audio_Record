package com.xxmrk888ytxx.videorecorder

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.xxmrk888ytxx.videorecorder.models.CameraRotation
import com.xxmrk888ytxx.videorecorder.models.CameraType
import com.xxmrk888ytxx.videorecorder.models.MaxQuality
import com.xxmrk888ytxx.videorecorder.models.RecorderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import java.io.File

interface VideoRecorder {

    val recordState:Flow<RecorderState>

    fun startRecord()

    fun pauseRecord()

    fun resumeRecord()

    fun stopRecordAndDestroyRecorder()

    class Builder(
        private val context: Context,
        private val outputFile: File,
        private val lifecycleOwner: LifecycleOwner
    ) {

        @OptIn(ExperimentalCoroutinesApi::class)
        val defaultScope : CoroutineScope
            get() = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))

        private var selectedScope: CoroutineScope? = null

        private var cameraType: CameraType = CameraType.Back

        private var maxQuality: MaxQuality = MaxQuality.HD

        private var cameraRotation:CameraRotation = CameraRotation.ROTATION_0

        fun setScope(coroutineScope: CoroutineScope): Builder {
            selectedScope = coroutineScope

            return this
        }

        fun setCameraType(cameraType: CameraType): Builder {
            this.cameraType = cameraType

            return this
        }

        fun setMaxQuality(maxQuality: MaxQuality): Builder {
            this.maxQuality = maxQuality

            return this
        }

        fun setCameraRotation(cameraRotation: CameraRotation) {
            this.cameraRotation = cameraRotation
        }

        fun build(): VideoRecorder {
            return VideoRecorderImpl(
                context,
                outputFile,
                lifecycleOwner,
                selectedScope ?: defaultScope,
                maxQuality,
                cameraType,
                cameraRotation
            )
        }
    }
}