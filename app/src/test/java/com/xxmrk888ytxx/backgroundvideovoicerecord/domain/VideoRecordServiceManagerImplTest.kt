package com.xxmrk888ytxx.backgroundvideovoicerecord.domain

import android.content.Context
import android.content.ServiceConnection
import android.util.Log
import com.xxmrk888ytxx.audiorecordservice.AudioRecordService
import com.xxmrk888ytxx.audiorecordservice.AudioRecordServiceController
import com.xxmrk888ytxx.backgroundvideovoicerecord.MainDispatcherRule
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManagerImpl
import com.xxmrk888ytxx.recordvideoservice.RecordVideoService
import com.xxmrk888ytxx.recordvideoservice.RecordVideoServiceController
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class VideoRecordServiceManagerImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val recordStateObserverScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private val serviceManagedScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private val videoRecordRepository: VideoRecordRepository = mockk(relaxed = true)

    private val binder: RecordVideoService.LocalBinder = mockk(relaxed = true)
    val controller: RecordVideoServiceController = mockk(relaxed = true)

    private lateinit var manager : VideoRecordServiceManager

    @Before
    fun init() {
        mockkStatic(Log::class)
        every { Log.i(any(),any()) } returns 1
        every { binder.controller } returns controller
        manager = spyk(VideoRecordServiceManagerImpl(context, recordStateObserverScope, serviceManagedScope, videoRecordRepository))
    }

    @Test
    fun `startRecord_send start record request when connection not setup expect manager start record`() = runBlocking {
        every { context.bindService(any(),manager as ServiceConnection,Context.BIND_AUTO_CREATE) } answers {
            val connection  = (this.args[1] as ServiceConnection)

            connection.onServiceConnected(mockk(),binder)

            true
        }

        val outputFile: File = mockk()

        coEvery { videoRecordRepository.getFileForRecord() } returns outputFile


        manager.startRecord()


        coVerifyOrder {
            context.bindService(any(),manager as ServiceConnection,Context.BIND_AUTO_CREATE)

            controller.startRecord(outputFile)
        }
    }

    @Test
    fun `startRecord_send start record request when connection setup expect manager start record`() = runBlocking {

        (manager as ServiceConnection).onServiceConnected(mockk(),binder)

        val outputFile: File = mockk()

        coEvery { videoRecordRepository.getFileForRecord() } returns outputFile


        manager.startRecord()


        coVerify(exactly = 1) {
            controller.startRecord(outputFile)
        }

        verify(exactly = 0) { context.bindService(any(),any(),any()) }
    }

    @Test
    fun `pauseRecord_send pause record request when connection not setup expect manager pause record`() = runBlocking {
        every { context.bindService(any(),manager as ServiceConnection,Context.BIND_AUTO_CREATE) } answers {
            val connection  = (this.args[1] as ServiceConnection)

            connection.onServiceConnected(mockk(),binder)

            true
        }


        manager.pauseRecord()


        coVerifyOrder {
            context.bindService(any(),manager as ServiceConnection,Context.BIND_AUTO_CREATE)

            controller.pauseRecord()
        }
    }

    @Test
    fun `pauseRecord_send pause record request when connection setup expect manager pause record`() = runBlocking {

        (manager as ServiceConnection).onServiceConnected(mockk(),binder)


        manager.pauseRecord()


        coVerify(exactly = 1) {
            controller.pauseRecord()
        }

        verify(exactly = 0) { context.bindService(any(),any(),any()) }
    }

    @Test
    fun `resumeRecord_send resume record request when connection not setup expect manager resume record`() = runBlocking {
        every { context.bindService(any(),manager as ServiceConnection,Context.BIND_AUTO_CREATE) } answers {
            val connection  = (this.args[1] as ServiceConnection)

            connection.onServiceConnected(mockk(),binder)

            true
        }


        manager.resumeRecord()


        coVerifyOrder {
            context.bindService(any(),manager as ServiceConnection,Context.BIND_AUTO_CREATE)

            controller.resumeRecord()
        }
    }

    @Test
    fun `resumeRecord_send resume record request when connection setup expect manager resume record`() = runBlocking {

        (manager as ServiceConnection).onServiceConnected(mockk(),binder)


        manager.resumeRecord()


        coVerify(exactly = 1) {
            controller.resumeRecord()
        }

        verify(exactly = 0) { context.bindService(any(),any(),any()) }
    }

    @Test
    fun `stopRecord_send stop record request when connection not setup expect manager stop record`() = runBlocking {
        every { context.bindService(any(),manager as ServiceConnection,Context.BIND_AUTO_CREATE) } answers {
            val connection  = (this.args[1] as ServiceConnection)

            connection.onServiceConnected(mockk(),binder)

            true
        }


        manager.stopRecord()


        coVerifyOrder {
            context.bindService(any(),manager as ServiceConnection,Context.BIND_AUTO_CREATE)

            controller.stopRecord()

            context.unbindService(manager as ServiceConnection)
        }
    }

    @Test
    fun `stopRecord_send stop record request when connection setup expect manager stop record`() = runBlocking {

        (manager as ServiceConnection).onServiceConnected(mockk(),binder)


        manager.stopRecord()


        coVerifyOrder() {
            controller.stopRecord()

            context.unbindService(manager as ServiceConnection)
        }

        verify(exactly = 0) { context.bindService(any(),any(),any()) }
    }
}