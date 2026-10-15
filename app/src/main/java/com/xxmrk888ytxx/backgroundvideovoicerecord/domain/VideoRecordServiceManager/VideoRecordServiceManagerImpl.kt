package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.RecordVideoStateObserverScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.VideoServiceManagedScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManagerImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
import com.xxmrk888ytxx.recordvideoservice.RecordVideoService
import com.xxmrk888ytxx.recordvideoservice.RecordVideoServiceController
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.ExternalStorageExportManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.videoProcessing.DualVideoCombiner
import com.xxmrk888ytxx.recordvideoscreen.models.DualCameraSaveOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class VideoRecordServiceManagerImpl @Inject constructor(
    private val context: Context,
    @RecordVideoStateObserverScopeQualifier private val recordStateObserverScope: CoroutineScope,
    @VideoServiceManagedScopeQualifier private val serviceManagedScope: CoroutineScope,
    private val videoRecordRepository: VideoRecordRepository,
    private val cameraConfigManager: CameraConfigManager,
    private val dualVideoCombiner: DualVideoCombiner,
    private val externalStorageExportManager: ExternalStorageExportManager
) : VideoRecordServiceManager,ServiceConnection {
    //Record state
    private val _currentRecordState:MutableStateFlow<RecordVideoState> = MutableStateFlow(RecordVideoState.Idle)

    override val currentRecordState: Flow<RecordVideoState> = _currentRecordState.asStateFlow()

    private val _hasPendingDualRecord: MutableStateFlow<Boolean> = MutableStateFlow(videoRecordRepository.hasPendingDualRecord())

    override val hasPendingDualRecord: Flow<Boolean> = _hasPendingDualRecord.asStateFlow()
    //

    //State observe
    private fun startObserver() {
        recordStateObserverScope.cancelChillersAndLaunch {
            controller?.run {
                var lastRecordState:RecordVideoState = RecordVideoState.Idle
                currentState.collect() { state ->
                    _currentRecordState.update { state }

                    if(
                        isConnected &&
                        _currentRecordState.value is RecordVideoState.Idle &&
                        lastRecordState !is RecordVideoState.Idle
                    ) {
                        this@VideoRecordServiceManagerImpl.stopRecord()
                    }

                    if (state is RecordVideoState.Idle) {
                        _hasPendingDualRecord.update { videoRecordRepository.hasPendingDualRecord() }
                    }

                    try {
                        android.service.quicksettings.TileService.requestListeningState(
                            context,
                            ComponentName(context, "com.xxmrk888ytxx.fastopenappquicksettingsservice.FastOpenAppQuickSettingsService")
                        )
                    } catch (_: Exception) {}

                    lastRecordState = state
                }
            }
        }
    }

    private fun stopObserver() {
        recordStateObserverScope.coroutineContext.cancelChildren()
    }
    //

    //Delayed control request
    private val delayedManageRequest: Queue<() -> Unit> = LinkedList()

    private suspend fun executeDelayedRequests() {
        Log.i(LOG_TAG,"Execute delayed requests")
        while (delayedManageRequest.isNotEmpty()) {
            delayedManageRequest.poll()?.invoke()
        }
    }
    //

    //Service connection

    override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
        serviceManagedScope.launch {
            Log.i(LOG_TAG,"Connected to service")
            val binder = (serviceBinder as? RecordVideoService.LocalBinder)

            controller = binder?.controller

            startObserver()
            executeDelayedRequests()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        serviceManagedScope.launch {
            Log.i(LOG_TAG,"Disconnected from service")
            controller = null

            stopObserver()
            _hasPendingDualRecord.update { videoRecordRepository.hasPendingDualRecord() }
        }
    }

    private val isConnected: Boolean
        get() = controller != null

    private suspend fun connectToService() {
        Log.i(LOG_TAG,"Connecting request")
        Intent(context, RecordVideoService::class.java).apply {
            context.bindService(
                this,
                this@VideoRecordServiceManagerImpl,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    private suspend fun disconnectFromService() {
        try {
            Log.i(LOG_TAG,"Disconnect request")
            context.unbindService(this)
            clearServiceConnection()
        }catch (e:Exception) {
            Log.d(LOG_TAG,"Error when unbind from service" + e.stackTraceToString())
        }
    }

    private suspend fun clearServiceConnection() {
        Log.i(LOG_TAG,"Clear Service Connection")

        controller = null

        delayedManageRequest.clear()

        recordStateObserverScope.coroutineContext.cancelChildren()

        _currentRecordState.update { RecordVideoState.Idle }
    }
    //

    //Control

    private var controller: RecordVideoServiceController? = null

    override fun startRecord() {
        Log.i(LOG_TAG,"request start record")
        serviceManagedScope.launch {
            if(!isConnected) {
                delayedManageRequest.add(::startRecord)

                connectToService()

                return@launch
            }

            val isDual = cameraConfigManager.config.first().cameraType is CameraType.Both
            val primaryFile = videoRecordRepository.getFileForRecord()
            val secondaryFile = if (isDual) videoRecordRepository.getSecondaryFileForRecord() else null

            controller?.startRecord(primaryFile, secondaryFile)
        }
    }

    override fun pauseRecord() {
        Log.i(LOG_TAG,"request pause record")
        serviceManagedScope.launch {
            if(!isConnected) {
                delayedManageRequest.add(::pauseRecord)

                connectToService()

                return@launch
            }

            controller?.pauseRecord()
        }
    }

    override fun resumeRecord() {
        Log.i(LOG_TAG,"request resume record")
        serviceManagedScope.launch {
            if(!isConnected) {
                delayedManageRequest.add(::resumeRecord)

                connectToService()

                return@launch
            }

            controller?.resumeRecord()
        }
    }

    override fun stopRecord() {
        Log.i(LOG_TAG,"request stop record")
        serviceManagedScope.launch {

            if(!isConnected) {
                delayedManageRequest.add(::stopRecord)

                connectToService()

                return@launch
            }

            controller?.stopRecord()

            disconnectFromService()

            _hasPendingDualRecord.update { videoRecordRepository.hasPendingDualRecord() }
        }
    }

    override suspend fun saveDualRecord(option: DualCameraSaveOption) = withContext(Dispatchers.IO) {
        val (primary, secondary) = videoRecordRepository.getPendingFiles()
        if (primary == null || !primary.exists()) {
            _hasPendingDualRecord.update { false }
            videoRecordRepository.cleanUpPendingRecords()
            return@withContext
        }

        try {
            when (option) {
                DualCameraSaveOption.BACK_ONLY -> {
                    val saved = videoRecordRepository.addCustomFile(primary, "Задняя камера")
                    externalStorageExportManager.exportNewVideoFile(saved)
                    secondary?.delete()
                }
                DualCameraSaveOption.FRONT_ONLY -> {
                    if (secondary != null && secondary.exists()) {
                        val saved = videoRecordRepository.addCustomFile(secondary, "Передняя камера")
                        externalStorageExportManager.exportNewVideoFile(saved)
                    }
                    primary.delete()
                }
                DualCameraSaveOption.BOTH_SEPARATELY -> {
                    val savedBack = videoRecordRepository.addCustomFile(primary, "Задняя камера")
                    externalStorageExportManager.exportNewVideoFile(savedBack)
                    if (secondary != null && secondary.exists()) {
                        val savedFront = videoRecordRepository.addCustomFile(secondary, "Передняя камера")
                        externalStorageExportManager.exportNewVideoFile(savedFront)
                    }
                }
                DualCameraSaveOption.BOTH_TOGETHER -> {
                    if (secondary != null && secondary.exists()) {
                        val combinedTemp = File(context.cacheDir, "temp_combined_${System.currentTimeMillis()}.mp4")
                        val success = dualVideoCombiner.combine(primary, secondary, combinedTemp)
                        if (success && combinedTemp.exists()) {
                            val saved = videoRecordRepository.addCustomFile(combinedTemp, "Обе камеры (вместе)")
                            externalStorageExportManager.exportNewVideoFile(saved)
                            primary.delete()
                            secondary.delete()
                        } else {
                            val savedBack = videoRecordRepository.addCustomFile(primary, "Задняя камера")
                            externalStorageExportManager.exportNewVideoFile(savedBack)
                            val savedFront = videoRecordRepository.addCustomFile(secondary, "Передняя камера")
                            externalStorageExportManager.exportNewVideoFile(savedFront)
                        }
                    } else {
                        val saved = videoRecordRepository.addCustomFile(primary, "Задняя камера")
                        externalStorageExportManager.exportNewVideoFile(saved)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error saving dual record: ${e.stackTraceToString()}")
        } finally {
            videoRecordRepository.cleanUpPendingRecords()
            _hasPendingDualRecord.update { false }
        }
    }

    //

    companion object {
        const val LOG_TAG = "VideoRecordServiceManagerImpl"
    }
}