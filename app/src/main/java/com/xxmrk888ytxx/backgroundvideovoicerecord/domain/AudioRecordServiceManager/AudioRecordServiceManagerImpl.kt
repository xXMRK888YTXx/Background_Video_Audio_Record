package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.xxmrk888ytxx.audiorecordservice.AudioRecordService
import com.xxmrk888ytxx.audiorecordservice.AudioRecordServiceController
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.RecordAudioStateObserverScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.AudioServiceManagedScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCase
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
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

class AudioRecordServiceManagerImpl @Inject constructor(
    private val context: Context,
    @RecordAudioStateObserverScopeQualifier private val recordStateObserverScope:CoroutineScope,
    @AudioServiceManagedScopeQualifier private val serviceManagedScope:CoroutineScope,
    private val audioRecordRepository: AudioRecordRepository
) : AudioRecordServiceManager,ServiceConnection {

    private val _currentRecordState:MutableStateFlow<RecordAudioState> = MutableStateFlow(
        RecordAudioState.Idle
    )

    override val currentRecordState: Flow<RecordAudioState> = _currentRecordState.asStateFlow()

    private val delayedManageRequest:Queue<() -> Unit> = LinkedList()

    var recordServiceController: AudioRecordServiceController? = null

    override fun startRecord() {
        serviceManagedScope.launch {
            if(!isConnectedToService()) {
                delayedManageRequest.add(::startRecord)
                connectToService()

                return@launch
            }

            recordServiceController?.startRecord(audioRecordRepository.getFileForRecord())

        }
    }

    override fun pauseRecord() {
        serviceManagedScope.launch {
            if(!isConnectedToService()) {
                delayedManageRequest.add(::pauseRecord)
                connectToService()

                return@launch
            }

            recordServiceController?.pauseRecord()

        }
    }

    override fun resumeRecord() {
        serviceManagedScope.launch {
            if(!isConnectedToService()) {
                delayedManageRequest.add(::resumeRecord)
                connectToService()

                return@launch
            }

            recordServiceController?.resumeRecord()

        }
    }

    override fun stopRecord() {
        serviceManagedScope.launch {
            if(!isConnectedToService()) {
                delayedManageRequest.add(::stopRecord)
                connectToService()

                return@launch
            }

            recordServiceController?.stopRecord()

        }
    }



    private suspend fun isConnectedToService() : Boolean {
        return recordServiceController != null
    }

    private suspend fun connectToService() {
        Intent(context,AudioRecordService::class.java).apply {
            context.bindService(
                this,
                this@AudioRecordServiceManagerImpl, Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
        val binder = serviceBinder as? AudioRecordService.LocalBinder

        recordServiceController = binder?.controller


        recordStateObserverScope.cancelChillersAndLaunch {
            recordServiceController?.let { controller ->
                controller.currentState.collect() { state ->
                    _currentRecordState.update { state }
                }
            }
        }

        executeDelayedRequests()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        recordServiceController = null

        recordStateObserverScope.coroutineContext.cancelChildren()
    }

    private fun executeDelayedRequests() {
        serviceManagedScope.launch {
            while(delayedManageRequest.isNotEmpty()) {
                delayedManageRequest.poll()?.invoke()
            }
        }
    }
}