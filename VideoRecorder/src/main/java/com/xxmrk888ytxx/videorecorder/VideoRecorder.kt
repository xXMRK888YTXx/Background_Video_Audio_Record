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

/**
 * [Ru]
 * Интерфейс который реализует рекордер для записи видео
 */

/**
 * [En]
 * Interface that implements a recorder for video recording
 */
interface VideoRecorder {

    /**
     * [Ru]
     * Возвращает текущее состояние рекордера
     */

    /**
     * [En]
     * Returns the current state of the recorder
     */
    val recordState:Flow<RecorderState>

    /**
     * [Ru]
     * Запускает запись видео
     *
     * Устанавливает состояние записи [RecorderState.Recording]
     */

    /**
     * [En]
     * Starts video recording
     *
     * Sets recording status [RecorderState.Recording]
     */
    fun startRecord()

    /**
     * [Ru]
     * Приостанавливает запись видео
     *
     * Устанавливает состояние записи [RecorderState.Paused]
     */

    /**
     * [En]
     * Suspends video recording
     *
     * Sets the recording status [RecorderState.Paused]
     */
    fun pauseRecord()

    /**
     * [Ru]
     * Возобновляет, ранее приостановленную запись видео
     *
     * Устанавливает состояние записи [RecorderState.Recording]
     */

    /**
     * [En]
     * Resumes previously paused video recording
     *
     * Sets the recording status [RecorderState.Recording]
     */
    fun resumeRecord()

    /**
     * [Ru]
     * Отстанавливает запись видео и уничтажает рекордер
     *
     * После вызова данного метода рекордер, не может быть больше использован
     *
     * Устанавливает состояние записи [RecorderState.Destroyed]
     */

    /**
     * [En]
     * Pauses video recording and destroys the recorder
     *
     * After calling this method, the recorder cannot be used anymore
     *
     * Sets the recording state [RecorderState.Destroyed]
     */
    fun stopRecordAndDestroyRecorder()

    /**
     * [Ru]
     * Билдер для создание [VideoRecorder]
     *
     * @param context - Контекст приложения
     * @param outputFile - файл в куда будет производится запись
     * @param lifecycleOwner - жизненный цикл к которому будет привязан рекордер
     *
     */

    /**
     * [En]
     * Bilder to create [VideoRecorder]
     *
     * @param context - Application context
     * @param outputFile - the file where the recording will be made
     * @param lifecycleOwner - lifecycle the recorder will be linked to
     *
     */
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

        /**
         * [Ru]
         * Устанавливает [CoroutineScope] на котором будет производится работа с камерой
         *
         * Переданный [CoroutineScope] будет закрыт после уничтожения рекордера
         */

        /**
         * [En]
         * Sets the [CoroutineScope] on which the camera will be operated
         *
         * The transmitted [CoroutineScope] will be closed after the recorder is destroyed
         */
        fun setScope(coroutineScope: CoroutineScope): Builder {
            selectedScope = coroutineScope

            return this
        }

        /**
         * [Ru]
         * Устанавливает камеру с которой будет производится запись
         *
         * По умолчанию, установлен [CameraType.Back]
         */

        /**
         * [En]
         * Sets the camera to be recorded from
         *
         * By default, it is set to [CameraType.Back].
         */
        fun setCameraType(cameraType: CameraType): Builder {
            this.cameraType = cameraType

            return this
        }

        /**
         * [Ru]
         *  Устанавливает максимальное качество записи.
         *
         *  Если камера не подерживает, переданное качество, то будет оно будет
         *  понижено до максимально возможного
         *
         *  Минимально возможное качествно [MaxQuality.SD]
         *
         *  По умолчанию, установлено [MaxQuality.HD]
         */
        fun setMaxQuality(maxQuality: MaxQuality): Builder {
            this.maxQuality = maxQuality

            return this
        }

        /**
         * [Ru]
         * Устанавливает поворот камеры
         *
         * По умалчанию уставновлен как [CameraRotation.ROTATION_0]
         */
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