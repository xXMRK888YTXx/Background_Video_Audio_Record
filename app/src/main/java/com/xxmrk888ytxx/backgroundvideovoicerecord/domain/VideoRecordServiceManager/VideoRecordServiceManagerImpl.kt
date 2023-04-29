package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.xxmrk888ytxx.audiorecordservice.AudioRecordService
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.RecordVideoStateObserverScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.VideoServiceManagedScopeQualifier
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
    @VideoServiceManagedScopeQualifier private val serviceManagedScope: CoroutineScope
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
        while (delayedManageRequest.isNotEmpty()) {
            delayedManageRequest.poll()?.invoke()
        }
    }
    //

    //Service connection

    override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
        serviceManagedScope.launch {
            val binder = (serviceBinder as? RecordVideoService.LocalBinder)

            controller = binder?.controller

            startObserver()
            executeDelayedRequests()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        serviceManagedScope.launch {
            controller = null

            stopObserver()
        }
    }

    private val isConnected: Boolean
        get() = controller != null

    private suspend fun connectToService() {
        Intent(context, RecordVideoService::class.java).apply {
            context.bindService(
                this,
                this@VideoRecordServiceManagerImpl,
                Context.BIND_AUTO_CREATE
            )
        }
    }
    //

    //Control

    private var controller: RecordVideoServiceController? = null

    override fun startRecord() {
        serviceManagedScope.launch {
            if(!isConnected) {
                delayedManageRequest.add(::startRecord)

                connectToService()

                return@launch
            }

            controller?.startRecord()
        }
    }

    override fun pauseRecord() {
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
        serviceManagedScope.launch {
            if(!isConnected) {
                delayedManageRequest.add(::stopRecord)

                connectToService()

                return@launch
            }

            controller?.stopRecord()
        }
    }

    //
}