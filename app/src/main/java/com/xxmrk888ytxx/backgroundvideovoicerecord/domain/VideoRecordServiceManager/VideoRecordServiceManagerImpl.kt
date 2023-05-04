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

class VideoRecordServiceManagerImpl @Inject constructor(
    private val context: Context,
    @RecordVideoStateObserverScopeQualifier private val recordStateObserverScope: CoroutineScope,
    @VideoServiceManagedScopeQualifier private val serviceManagedScope: CoroutineScope,
    private val videoRecordRepository: VideoRecordRepository
) : VideoRecordServiceManager,ServiceConnection {
    //Record state
    private val _currentRecordState:MutableStateFlow<RecordVideoState> = MutableStateFlow(RecordVideoState.Idle)

    override val currentRecordState: Flow<RecordVideoState> = _currentRecordState.asStateFlow()
    //

    //State observe
    private fun startObserver() {
        recordStateObserverScope.cancelChillersAndLaunch {
            controller?.run {
                currentState.collect() { state ->
                    _currentRecordState.update { state }
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

            controller?.startRecord(videoRecordRepository.getFileForRecord())
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
        }
    }

    //

    companion object {
        const val LOG_TAG = "VideoRecordServiceManagerImpl"
    }
}