package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.xxmrk888ytxx.audiorecordservice.AudioRecordService
import com.xxmrk888ytxx.audiorecordservice.AudioRecordServiceController
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.RecordAudioStateObserverScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.AudioServiceManagedScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCase
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            Log.i(LOG_TAG,"startRecord request")
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
            Log.i(LOG_TAG,"pause request")
            if(!isConnectedToService()) {
                delayedManageRequest.add(::pauseRecord)
                connectToService()

                return@launch
            }

            recordServiceController?.pauseRecord()

        }
    }

    override fun resumeRecord() {
        Log.i(LOG_TAG,"resume request")
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
        Log.i(LOG_TAG,"stop request")
        serviceManagedScope.launch {
            if(!isConnectedToService()) {
                delayedManageRequest.add(::stopRecord)
                connectToService()

                return@launch
            }

            recordServiceController?.stopRecord()

            disconnectFromService()
        }
    }



    private suspend fun isConnectedToService() : Boolean {
        return recordServiceController != null
    }

    private suspend fun connectToService() {
        Log.i(LOG_TAG,"Connected request")
        Intent(context,AudioRecordService::class.java).apply {
            context.bindService(
                this,
                this@AudioRecordServiceManagerImpl, Context.BIND_AUTO_CREATE
            )
        }
    }

    private suspend fun disconnectFromService() {
        try {
            Log.i(LOG_TAG,"Disconnected request")
            context.unbindService(this)
            clearServiceConnection()
        }catch (e:Exception) {
            Log.d(LOG_TAG,"Error when unbind from service" + e.stackTraceToString())
        }
    }

    private suspend fun clearServiceConnection() {
        Log.i(LOG_TAG,"Clear Service Connection")

        recordServiceController = null

        delayedManageRequest.clear()

        recordStateObserverScope.coroutineContext.cancelChildren()

        _currentRecordState.update { RecordAudioState.Idle }
    }

    override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
        Log.i(LOG_TAG,"connected to service")
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
        serviceManagedScope.launch {
            Log.i(LOG_TAG,"disconnected from service")

            clearServiceConnection()
        }

    }

    private fun executeDelayedRequests() {
        serviceManagedScope.launch {
            Log.i(LOG_TAG,"Execute delayed requests")
            while(delayedManageRequest.isNotEmpty()) {
                delayedManageRequest.poll()?.invoke()
            }
        }
    }

    companion object {
        const val LOG_TAG = "AudioRecordServiceManagerImpl"
    }
}