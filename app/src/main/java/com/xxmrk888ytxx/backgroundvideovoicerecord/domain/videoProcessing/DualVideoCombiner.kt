package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.videoProcessing

import android.os.Build
import android.graphics.SurfaceTexture
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import android.view.Surface
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.inject.Inject

import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition

private const val EGL_RECORDABLE_ANDROID = 0x3142

@AppScope
class DualVideoCombiner @Inject constructor() {

    suspend fun combine(
        primaryFile: File,
        secondaryFile: File,
        outputFile: File,
        mainCamera: DualCameraPrimary = DualCameraPrimary.BACK,
        pipPosition: PipPosition = PipPosition.TOP_RIGHT,
        onProgress: (Float) -> Unit = {}
    ): Boolean = withContext(Dispatchers.IO) {
        if (!primaryFile.exists() || primaryFile.length() == 0L) {
            Log.e(TAG, "Primary file does not exist or empty: ${primaryFile.absolutePath}")
            return@withContext false
        }

        if (outputFile.exists()) {
            outputFile.delete()
        }

        try {
            val success = combineInternal(primaryFile, secondaryFile, outputFile, mainCamera, pipPosition, onProgress)
            if (success && outputFile.exists() && outputFile.length() > 1000L) {
                Log.i(TAG, "Dual video combine succeeded! Size: ${outputFile.length()} bytes")
                return@withContext true
            } else {
                Log.e(TAG, "Dual video combine produced empty or invalid file")
                if (outputFile.exists()) outputFile.delete()
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error combining dual camera videos: ${e.stackTraceToString()}")
            if (outputFile.exists()) outputFile.delete()
            return@withContext false
        }
    }

    private class MuxerState {
        var started: Boolean = false
        var videoTrack: Int = -1
        var audioTrack: Int = -1
        var videoFramesWritten: Int = 0
    }

    private data class OutputDimensions(
        val width: Int,
        val height: Int,
        val orientationHint: Int
    )

    private fun resolveOptimalDimensions(sourceWidth: Int, sourceHeight: Int): OutputDimensions {
        val isSourceSdOrHd = minOf(sourceWidth, sourceHeight) <= 720
        val candidates = if (isSourceSdOrHd) {
            listOf(
                Pair(720, 1280),
                Pair(1080, 1920),
                Pair(540, 960)
            )
        } else {
            listOf(
                Pair(1080, 1920),
                Pair(720, 1280),
                Pair(540, 960)
            )
        }

        try {
            val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            for (info in codecList.codecInfos) {
                if (!info.isEncoder) continue
                val types = info.supportedTypes
                if (!types.contains(MediaFormat.MIMETYPE_VIDEO_AVC)) continue
                val videoCaps = info.getCapabilitiesForType(MediaFormat.MIMETYPE_VIDEO_AVC)?.videoCapabilities ?: continue
                for ((w, h) in candidates) {
                    if (videoCaps.isSizeSupported(w, h)) {
                        Log.i(TAG, "Selected portrait AVC resolution: ${w}x${h} supported by ${info.name}")
                        return OutputDimensions(w, h, 0)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed checking AVC encoder capabilities", e)
        }

        // Universal portrait default
        return OutputDimensions(720, 1280, 0)
    }

    private fun findHardwareEncoder(mimeType: String): MediaCodecInfo? {
        try {
            val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            for (info in codecList.codecInfos) {
                if (!info.isEncoder) continue
                val types = info.supportedTypes
                if (!types.contains(mimeType)) continue

                val isHardware = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    info.isHardwareAccelerated && !info.isSoftwareOnly
                } else {
                    val name = info.name.lowercase()
                    !name.startsWith("omx.google.") &&
                    !name.startsWith("c2.android.") &&
                    !name.contains("sw") &&
                    !name.contains("software")
                }

                if (isHardware) {
                    val caps = info.getCapabilitiesForType(mimeType)
                    val supportsSurface = caps.colorFormats.contains(MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                    if (supportsSurface) {
                        Log.i(TAG, "Hardware encoder discovered: ${info.name}")
                        return info
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed searching for hardware encoder: ${e.message}")
        }
        return null
    }

    private fun findHardwareDecoder(mimeType: String): MediaCodecInfo? {
        try {
            val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            for (info in codecList.codecInfos) {
                if (info.isEncoder) continue
                val types = info.supportedTypes
                if (!types.contains(mimeType)) continue

                val isHardware = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    info.isHardwareAccelerated && !info.isSoftwareOnly
                } else {
                    val name = info.name.lowercase()
                    !name.startsWith("omx.google.") &&
                    !name.startsWith("c2.android.") &&
                    !name.contains("sw") &&
                    !name.contains("software")
                }

                if (isHardware) {
                    Log.i(TAG, "Hardware decoder discovered: ${info.name}")
                    return info
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed searching for hardware decoder: ${e.message}")
        }
        return null
    }

    private fun combineInternal(
        primaryFile: File,
        secondaryFile: File,
        outputFile: File,
        mainCamera: DualCameraPrimary,
        pipPosition: PipPosition,
        onProgress: (Float) -> Unit
    ): Boolean {
        val hasValidSecondary = secondaryFile.exists() && secondaryFile.length() > 1000L
        val isMainFront = (mainCamera == DualCameraPrimary.FRONT && hasValidSecondary)

        // Select which file is fullscreen and which is PiP
        val fullScreenFile = if (isMainFront) secondaryFile else primaryFile
        val pipFile = if (isMainFront) primaryFile else secondaryFile

        val primaryExtractor = MediaExtractor().apply { setDataSource(fullScreenFile.absolutePath) }
        var secondaryExtractor: MediaExtractor? = null
        if (hasValidSecondary) {
            try {
                secondaryExtractor = MediaExtractor().apply { setDataSource(pipFile.absolutePath) }
            } catch (e: Exception) {
                Log.w(TAG, "Cannot open pip file extractor", e)
                secondaryExtractor = null
            }
        }

        var primaryVideoTrack = -1
        var primaryAudioTrack = -1
        for (i in 0 until primaryExtractor.trackCount) {
            val format = primaryExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mime.startsWith("video/") && primaryVideoTrack < 0) {
                primaryVideoTrack = i
            } else if (mime.startsWith("audio/") && primaryAudioTrack < 0) {
                primaryAudioTrack = i
            }
        }

        // If fullscreen file has no audio, attempt fallback to other file for audio
        var audioExtractor: MediaExtractor? = null
        if (primaryAudioTrack >= 0) {
            audioExtractor = MediaExtractor().apply {
                setDataSource(fullScreenFile.absolutePath)
                selectTrack(primaryAudioTrack)
            }
        } else if (secondaryExtractor != null) {
            for (i in 0 until secondaryExtractor.trackCount) {
                val format = secondaryExtractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
                if (mime.startsWith("audio/")) {
                    primaryAudioTrack = i
                    audioExtractor = MediaExtractor().apply {
                        setDataSource(pipFile.absolutePath)
                        selectTrack(i)
                    }
                    break
                }
            }
        }

        var secondaryVideoTrack = -1
        if (secondaryExtractor != null) {
            for (i in 0 until secondaryExtractor.trackCount) {
                val format = secondaryExtractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
                if (mime.startsWith("video/") && secondaryVideoTrack < 0) {
                    secondaryVideoTrack = i
                    break
                }
            }
        }

        if (primaryVideoTrack < 0) {
            primaryExtractor.release()
            secondaryExtractor?.release()
            audioExtractor?.release()
            throw IllegalStateException("Main video has no video track")
        }

        primaryExtractor.selectTrack(primaryVideoTrack)
        if (secondaryExtractor != null && secondaryVideoTrack >= 0) {
            secondaryExtractor.selectTrack(secondaryVideoTrack)
        }

        val primaryVideoFormat = primaryExtractor.getTrackFormat(primaryVideoTrack)
        val secondaryVideoFormat = if (secondaryExtractor != null && secondaryVideoTrack >= 0) {
            secondaryExtractor.getTrackFormat(secondaryVideoTrack)
        } else null

        val pRetriever = MediaMetadataRetriever()
        var pRetrieverRotation = 0
        var durationUs = 1000_000L
        try {
            pRetriever.setDataSource(fullScreenFile.absolutePath)
            pRetrieverRotation = pRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            durationUs = (pRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 1000L) * 1000L
            pRetriever.release()
        } catch (_: Exception) {}

        var sRetrieverRotation = 0
        if (hasValidSecondary) {
            try {
                val sRetriever = MediaMetadataRetriever()
                sRetriever.setDataSource(pipFile.absolutePath)
                sRetrieverRotation = sRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
                sRetriever.release()
            } catch (_: Exception) {}
        }

        val pCodedWidth = primaryVideoFormat.getInteger(MediaFormat.KEY_WIDTH)
        val pCodedHeight = primaryVideoFormat.getInteger(MediaFormat.KEY_HEIGHT)

        val sCodedWidth = secondaryVideoFormat?.getInteger(MediaFormat.KEY_WIDTH) ?: pCodedWidth
        val sCodedHeight = secondaryVideoFormat?.getInteger(MediaFormat.KEY_HEIGHT) ?: pCodedHeight

        // Set explicit rotation on decoders: MediaCodec natively handles KEY_ROTATION
        // and SurfaceTexture.getTransformMatrix() incorporates it into the GL matrix.
        val defaultMainRotation = if (isMainFront) 270 else 90
        val pRotation = when {
            primaryVideoFormat.containsKey(MediaFormat.KEY_ROTATION) -> primaryVideoFormat.getInteger(MediaFormat.KEY_ROTATION)
            pRetrieverRotation != 0 -> pRetrieverRotation
            pCodedWidth > pCodedHeight -> defaultMainRotation
            else -> 0
        }
        primaryVideoFormat.setInteger(MediaFormat.KEY_ROTATION, pRotation)

        if (secondaryVideoFormat != null) {
            val defaultPipRotation = if (isMainFront) 90 else 270
            val sRotation = when {
                secondaryVideoFormat.containsKey(MediaFormat.KEY_ROTATION) -> secondaryVideoFormat.getInteger(MediaFormat.KEY_ROTATION)
                sRetrieverRotation != 0 -> sRetrieverRotation
                sCodedWidth > sCodedHeight -> defaultPipRotation
                else -> 0
            }
            secondaryVideoFormat.setInteger(MediaFormat.KEY_ROTATION, sRotation)
        }

        Log.i(TAG, "Configuring decoders with KEY_ROTATION: primary=$pRotation, secondary=${secondaryVideoFormat?.getInteger(MediaFormat.KEY_ROTATION)}, isMainFront=$isMainFront")

        // Strictly vertical portrait output (width < height, e.g. 1080x1920 or 720x1280)
        val outputDims = resolveOptimalDimensions(pCodedWidth, pCodedHeight)
        val outWidth = outputDims.width
        val outHeight = outputDims.height
        Log.i(TAG, "Output canvas portrait dimensions: ${outWidth}x${outHeight}, orientationHint: ${outputDims.orientationHint}")

        // PiP strictly vertical 9:16 portrait in chosen position
        val pipWidth = (outWidth * 0.30f).toInt()
        val pipHeight = (pipWidth * 16 / 9)
        val marginX = (outWidth * 0.04f).toInt().coerceAtLeast(16)
        val marginY = (outHeight * 0.04f).toInt().coerceAtLeast(16)
        val (pipX, pipY) = when (pipPosition) {
            PipPosition.TOP_RIGHT -> Pair(
                (outWidth - pipWidth - marginX).coerceAtLeast(0),
                (outHeight - pipHeight - marginY).coerceAtLeast(0)
            )
            PipPosition.TOP_LEFT -> Pair(
                marginX,
                (outHeight - pipHeight - marginY).coerceAtLeast(0)
            )
            PipPosition.BOTTOM_RIGHT -> Pair(
                (outWidth - pipWidth - marginX).coerceAtLeast(0),
                marginY
            )
            PipPosition.BOTTOM_LEFT -> Pair(
                marginX,
                marginY
            )
        }

        // Setup Hardware-Accelerated AVC Encoder
        val encoderFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, outWidth, outHeight).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_BIT_RATE, 5_000_000)
            setInteger(MediaFormat.KEY_FRAME_RATE, 30)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            try {
                setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR)
            } catch (_: Exception) {}
        }

        val hwEncoder = findHardwareEncoder(MediaFormat.MIMETYPE_VIDEO_AVC)
        val encoder = if (hwEncoder != null) {
            try {
                Log.i(TAG, "Initializing hardware encoder: ${hwEncoder.name}")
                MediaCodec.createByCodecName(hwEncoder.name)
            } catch (e: Exception) {
                Log.w(TAG, "Failed creating hardware encoder by name, fallback to type", e)
                MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            }
        } else {
            Log.i(TAG, "No hardware encoder found, fallback to type")
            MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        }

        try {
            encoder.configure(encoderFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        } catch (e: Exception) {
            Log.w(TAG, "Failed configuring with VBR, retrying with standard format", e)
            val fallbackFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, outWidth, outHeight).apply {
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                setInteger(MediaFormat.KEY_BIT_RATE, 5_000_000)
                setInteger(MediaFormat.KEY_FRAME_RATE, 30)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            }
            encoder.configure(fallbackFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        }
        val inputSurface = encoder.createInputSurface()
        encoder.start()

        // Setup EGL on encoder input surface
        // Normal canonical coordinates (no mirror, 0 deg)
        val normalTexCoords = floatArrayOf(
            0.0f, 0.0f, // BL
            1.0f, 0.0f, // BR
            0.0f, 1.0f, // TL
            1.0f, 1.0f  // TR
        )
        // Mirrored selfie coordinates (horizontal mirror, 0 deg)
        val mirroredTexCoords = floatArrayOf(
            1.0f, 0.0f, // BL
            0.0f, 0.0f, // BR
            1.0f, 1.0f, // TL
            0.0f, 1.0f  // TR
        )

        val primaryTexCoords = if (isMainFront) mirroredTexCoords else normalTexCoords
        val secondaryTexCoords = if (isMainFront) normalTexCoords else mirroredTexCoords

        val eglCore = EglCore(
            surface = inputSurface,
            width = outWidth,
            height = outHeight,
            primaryTexCoords = primaryTexCoords,
            secondaryTexCoords = secondaryTexCoords
        )
        eglCore.makeCurrent()

        // Textures for decoders
        val primaryTexId = eglCore.createTextureObject()
        val primarySurfaceTexture = SurfaceTexture(primaryTexId).apply {
            setDefaultBufferSize(pCodedWidth, pCodedHeight)
        }
        val primaryDecoderSurface = Surface(primarySurfaceTexture)

        val secondaryTexId = eglCore.createTextureObject()
        val secondarySurfaceTexture = SurfaceTexture(secondaryTexId).apply {
            setDefaultBufferSize(sCodedWidth, sCodedHeight)
        }
        val secondaryDecoderSurface = Surface(secondarySurfaceTexture)

        // Setup hardware-accelerated decoders with KEY_ROTATION
        val primaryDecoderMime = primaryVideoFormat.getString(MediaFormat.KEY_MIME) ?: MediaFormat.MIMETYPE_VIDEO_AVC
        val primaryHwDec = findHardwareDecoder(primaryDecoderMime)
        val primaryDecoder = if (primaryHwDec != null) {
            try {
                Log.i(TAG, "Initializing hardware primary decoder: ${primaryHwDec.name}")
                MediaCodec.createByCodecName(primaryHwDec.name)
            } catch (e: Exception) {
                Log.w(TAG, "Hardware primary decoder instantiation failed, fallback to type", e)
                MediaCodec.createDecoderByType(primaryDecoderMime)
            }
        } else {
            MediaCodec.createDecoderByType(primaryDecoderMime)
        }
        primaryDecoder.configure(primaryVideoFormat, primaryDecoderSurface, null, 0)
        primaryDecoder.start()

        val secondaryDecoder = if (secondaryVideoFormat != null) {
            try {
                val secMime = secondaryVideoFormat.getString(MediaFormat.KEY_MIME) ?: MediaFormat.MIMETYPE_VIDEO_AVC
                val secHwDec = findHardwareDecoder(secMime)
                val decoder = if (secHwDec != null) {
                    try {
                        Log.i(TAG, "Initializing hardware secondary decoder: ${secHwDec.name}")
                        MediaCodec.createByCodecName(secHwDec.name)
                    } catch (e: Exception) {
                        Log.w(TAG, "Hardware secondary decoder instantiation failed, fallback to type", e)
                        MediaCodec.createDecoderByType(secMime)
                    }
                } else {
                    MediaCodec.createDecoderByType(secMime)
                }
                decoder.configure(secondaryVideoFormat, secondaryDecoderSurface, null, 0)
                decoder.start()
                decoder
            } catch (e: Exception) {
                Log.w(TAG, "Failed to create secondary decoder", e)
                null
            }
        } else null

        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4).apply {
            setOrientationHint(outputDims.orientationHint)
        }
        val muxerState = MuxerState()

        val bufferInfo = MediaCodec.BufferInfo()

        var primaryInputDone = false
        var primaryOutputDone = false
        var secondaryInputDone = false
        var secondaryOutputDone = false

        val stMatrixPrimary = FloatArray(16)
        val stMatrixSecondary = FloatArray(16)

        try {
            while (!primaryOutputDone) {
                // Feed primary decoder
                if (!primaryInputDone) {
                    val inIndex = primaryDecoder.dequeueInputBuffer(10_000)
                    if (inIndex >= 0) {
                        val byteBuffer = primaryDecoder.getInputBuffer(inIndex)!!
                        val sampleSize = primaryExtractor.readSampleData(byteBuffer, 0)
                        if (sampleSize < 0) {
                            primaryDecoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            primaryInputDone = true
                        } else {
                            primaryDecoder.queueInputBuffer(inIndex, 0, sampleSize, primaryExtractor.sampleTime, 0)
                            primaryExtractor.advance()
                        }
                    }
                }

                // Feed secondary decoder
                if (secondaryDecoder != null && secondaryExtractor != null && !secondaryInputDone) {
                    val inIndex = secondaryDecoder.dequeueInputBuffer(10_000)
                    if (inIndex >= 0) {
                        val byteBuffer = secondaryDecoder.getInputBuffer(inIndex)!!
                        val sampleSize = secondaryExtractor.readSampleData(byteBuffer, 0)
                        if (sampleSize < 0) {
                            secondaryDecoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            secondaryInputDone = true
                        } else {
                            secondaryDecoder.queueInputBuffer(inIndex, 0, sampleSize, secondaryExtractor.sampleTime, 0)
                            secondaryExtractor.advance()
                        }
                    }
                }

                // Drain secondary decoder frames
                if (secondaryDecoder != null && !secondaryOutputDone) {
                    val secOutIndex = secondaryDecoder.dequeueOutputBuffer(bufferInfo, 0)
                    if (secOutIndex >= 0) {
                        val isEOS = (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                        secondaryDecoder.releaseOutputBuffer(secOutIndex, !isEOS)
                        if (isEOS) secondaryOutputDone = true
                    }
                }

                // Process primary output frame
                val outIndex = primaryDecoder.dequeueOutputBuffer(bufferInfo, 10_000)
                if (outIndex >= 0) {
                    val isEOS = (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                    if (bufferInfo.size > 0 && !isEOS) {
                        primaryDecoder.releaseOutputBuffer(outIndex, true)
                        primarySurfaceTexture.updateTexImage()
                        primarySurfaceTexture.getTransformMatrix(stMatrixPrimary)

                        if (secondaryDecoder != null) {
                            try {
                                secondarySurfaceTexture.updateTexImage()
                                secondarySurfaceTexture.getTransformMatrix(stMatrixSecondary)
                            } catch (_: Exception) {}
                        }

                        // Draw composite frame
                        eglCore.drawCompositeFrame(
                            primaryTexId = primaryTexId,
                            primaryMatrix = stMatrixPrimary,
                            secondaryTexId = if (secondaryDecoder != null) secondaryTexId else null,
                            secondaryMatrix = stMatrixSecondary,
                            width = outWidth,
                            height = outHeight,
                            pipX = pipX,
                            pipY = pipY,
                            pipWidth = pipWidth,
                            pipHeight = pipHeight
                        )

                        eglCore.setPresentationTime(bufferInfo.presentationTimeUs * 1000L)
                        eglCore.swapBuffers()

                        if (durationUs > 0) {
                            val progress = (bufferInfo.presentationTimeUs.toFloat() / durationUs.toFloat()).coerceIn(0f, 1f)
                            onProgress(progress)
                        }
                    } else {
                        primaryDecoder.releaseOutputBuffer(outIndex, false)
                    }

                    if (isEOS) {
                        primaryOutputDone = true
                        encoder.signalEndOfInputStream()
                    }
                }

                // Drain encoder
                drainEncoder(encoder, bufferInfo, muxer, audioExtractor, primaryAudioTrack, muxerState, false)
            }

            // Drain remaining encoder frames until true EOS from encoder
            drainEncoderUntilEos(encoder, bufferInfo, muxer, audioExtractor, primaryAudioTrack, muxerState)

            // Remux audio track directly from primary with strictly monotonic presentation times
            if (audioExtractor != null && muxerState.audioTrack >= 0 && muxerState.started) {
                audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
                val audioBuffer = ByteBuffer.allocateDirect(256 * 1024)
                val audioBufferInfo = MediaCodec.BufferInfo()

                var lastAudioPts = -1L
                while (true) {
                    val sampleSize = audioExtractor.readSampleData(audioBuffer, 0)
                    if (sampleSize < 0) break

                    val pts = audioExtractor.sampleTime
                    if (pts > lastAudioPts) {
                        audioBufferInfo.offset = 0
                        audioBufferInfo.size = sampleSize
                        audioBufferInfo.presentationTimeUs = pts
                        audioBufferInfo.flags = audioExtractor.sampleFlags
                        muxer.writeSampleData(muxerState.audioTrack, audioBuffer, audioBufferInfo)
                        lastAudioPts = pts
                    }
                    audioExtractor.advance()
                }
            }

            return muxerState.started && muxerState.videoFramesWritten > 0
        } finally {
            try { primaryDecoder.stop(); primaryDecoder.release() } catch (_: Exception) {}
            try { secondaryDecoder?.stop(); secondaryDecoder?.release() } catch (_: Exception) {}
            try { encoder.stop(); encoder.release() } catch (_: Exception) {}
            try { primarySurfaceTexture.release(); primaryDecoderSurface.release() } catch (_: Exception) {}
            try { secondarySurfaceTexture.release(); secondaryDecoderSurface.release() } catch (_: Exception) {}
            try { eglCore.release() } catch (_: Exception) {}
            try { primaryExtractor.release() } catch (_: Exception) {}
            try { secondaryExtractor?.release() } catch (_: Exception) {}
            try { audioExtractor?.release() } catch (_: Exception) {}
            try {
                if (muxerState.started) {
                    muxer.stop()
                    muxer.release()
                }
            } catch (_: Exception) {}
        }
    }

    private fun drainEncoder(
        encoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        muxer: MediaMuxer,
        audioExtractor: MediaExtractor?,
        primaryAudioTrack: Int,
        muxerState: MuxerState,
        endOfStream: Boolean
    ) {
        val timeoutUs = if (endOfStream) 10_000L else 0L
        while (true) {
            val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, timeoutUs)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                break
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (!muxerState.started) {
                    val newFormat = encoder.outputFormat
                    Log.i(TAG, "Encoder output format changed: $newFormat")
                    muxerState.videoTrack = muxer.addTrack(newFormat)
                    if (audioExtractor != null && primaryAudioTrack >= 0) {
                        val audioFormat = audioExtractor.getTrackFormat(primaryAudioTrack)
                        muxerState.audioTrack = muxer.addTrack(audioFormat)
                    }
                    muxer.start()
                    muxerState.started = true
                }
            } else if (encoderStatus >= 0) {
                val encodedData = encoder.getOutputBuffer(encoderStatus)
                if (encodedData != null) {
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        bufferInfo.size = 0
                    }

                    if (bufferInfo.size > 0) {
                        if (!muxerState.started) {
                            val newFormat = encoder.outputFormat
                            muxerState.videoTrack = muxer.addTrack(newFormat)
                            if (audioExtractor != null && primaryAudioTrack >= 0) {
                                val audioFormat = audioExtractor.getTrackFormat(primaryAudioTrack)
                                muxerState.audioTrack = muxer.addTrack(audioFormat)
                            }
                            muxer.start()
                            muxerState.started = true
                        }

                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        muxer.writeSampleData(muxerState.videoTrack, encodedData, bufferInfo)
                        muxerState.videoFramesWritten++
                    }
                }

                val isEOS = (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                encoder.releaseOutputBuffer(encoderStatus, false)
                if (isEOS) break
            }
        }
    }

    private fun drainEncoderUntilEos(
        encoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        muxer: MediaMuxer,
        audioExtractor: MediaExtractor?,
        primaryAudioTrack: Int,
        muxerState: MuxerState
    ) {
        var eosReceived = false
        var attempts = 0
        while (!eosReceived && attempts < 80) {
            val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, 15_000)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                attempts++
                continue
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (!muxerState.started) {
                    val newFormat = encoder.outputFormat
                    muxerState.videoTrack = muxer.addTrack(newFormat)
                    if (audioExtractor != null && primaryAudioTrack >= 0) {
                        val audioFormat = audioExtractor.getTrackFormat(primaryAudioTrack)
                        muxerState.audioTrack = muxer.addTrack(audioFormat)
                    }
                    muxer.start()
                    muxerState.started = true
                }
            } else if (encoderStatus >= 0) {
                val encodedData = encoder.getOutputBuffer(encoderStatus)
                if (encodedData != null) {
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        bufferInfo.size = 0
                    }
                    if (bufferInfo.size > 0 && muxerState.started) {
                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        muxer.writeSampleData(muxerState.videoTrack, encodedData, bufferInfo)
                        muxerState.videoFramesWritten++
                    }
                }
                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    eosReceived = true
                }
                encoder.releaseOutputBuffer(encoderStatus, false)
            }
        }
    }

    companion object {
        private const val TAG = "DualVideoCombiner"
    }

    private class EglCore(
        private val surface: Surface,
        private val width: Int,
        private val height: Int,
        primaryTexCoords: FloatArray,
        secondaryTexCoords: FloatArray
    ) {
        private var eglDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
        private var eglContext: EGLContext = EGL14.EGL_NO_CONTEXT
        private var eglSurface: EGLSurface = EGL14.EGL_NO_SURFACE

        private var programId: Int = 0
        private var aPositionHandle: Int = 0
        private var aTexCoordHandle: Int = 0
        private var uSTMatrixHandle: Int = 0
        private var uSamplerHandle: Int = 0

        private val fullQuadBuffer: FloatBuffer
        private val primaryTexCoordBuffer: FloatBuffer
        private val secondaryTexCoordBuffer: FloatBuffer

        init {
            val fullQuad = floatArrayOf(
                -1.0f, -1.0f,
                 1.0f, -1.0f,
                -1.0f,  1.0f,
                 1.0f,  1.0f
            )

            fullQuadBuffer = ByteBuffer.allocateDirect(fullQuad.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(fullQuad)
            fullQuadBuffer.position(0)

            primaryTexCoordBuffer = ByteBuffer.allocateDirect(primaryTexCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(primaryTexCoords)
            primaryTexCoordBuffer.position(0)

            secondaryTexCoordBuffer = ByteBuffer.allocateDirect(secondaryTexCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(secondaryTexCoords)
            secondaryTexCoordBuffer.position(0)

            initEGL()
            initGL()
        }

        private fun initEGL() {
            eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            val version = IntArray(2)
            EGL14.eglInitialize(eglDisplay, version, 0, version, 1)

            val configAttribs = intArrayOf(
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL_RECORDABLE_ANDROID, 1,
                EGL14.EGL_NONE
            )
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            EGL14.eglChooseConfig(eglDisplay, configAttribs, 0, configs, 0, configs.size, numConfigs, 0)
            val config = configs[0]!!

            val contextAttribs = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
            )
            eglContext = EGL14.eglCreateContext(eglDisplay, config, EGL14.EGL_NO_CONTEXT, contextAttribs, 0)

            val surfaceAttribs = intArrayOf(EGL14.EGL_NONE)
            eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, config, surface, surfaceAttribs, 0)
        }

        private fun initGL() {
            makeCurrent()

            val vertexShaderCode = """
                attribute vec4 aPosition;
                attribute vec4 aTextureCoord;
                uniform mat4 uSTMatrix;
                varying vec2 vTextureCoord;
                void main() {
                    gl_Position = aPosition;
                    vTextureCoord = (uSTMatrix * aTextureCoord).xy;
                }
            """.trimIndent()

            val fragmentShaderCode = """
                #extension GL_OES_EGL_image_external : require
                precision mediump float;
                varying vec2 vTextureCoord;
                uniform samplerExternalOES sTexture;
                void main() {
                    gl_FragColor = texture2D(sTexture, vTextureCoord);
                }
            """.trimIndent()

            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            programId = GLES20.glCreateProgram().also {
                GLES20.glAttachShader(it, vertexShader)
                GLES20.glAttachShader(it, fragmentShader)
                GLES20.glLinkProgram(it)
            }

            aPositionHandle = GLES20.glGetAttribLocation(programId, "aPosition")
            aTexCoordHandle = GLES20.glGetAttribLocation(programId, "aTextureCoord")
            uSTMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix")
            uSamplerHandle = GLES20.glGetUniformLocation(programId, "sTexture")
        }

        fun makeCurrent() {
            EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
        }

        fun setPresentationTime(nsecs: Long) {
            android.opengl.EGLExt.eglPresentationTimeANDROID(eglDisplay, eglSurface, nsecs)
        }

        fun swapBuffers() {
            EGL14.eglSwapBuffers(eglDisplay, eglSurface)
        }

        fun createTextureObject(): Int {
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            return textures[0]
        }

        fun drawCompositeFrame(
            primaryTexId: Int,
            primaryMatrix: FloatArray,
            secondaryTexId: Int?,
            secondaryMatrix: FloatArray,
            width: Int,
            height: Int,
            pipX: Int,
            pipY: Int,
            pipWidth: Int,
            pipHeight: Int
        ) {
            // Step 1: Draw primary fullscreen
            GLES20.glViewport(0, 0, width, height)
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            GLES20.glUseProgram(programId)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, primaryTexId)
            GLES20.glUniform1i(uSamplerHandle, 0)
            GLES20.glUniformMatrix4fv(uSTMatrixHandle, 1, false, primaryMatrix, 0)

            GLES20.glEnableVertexAttribArray(aPositionHandle)
            GLES20.glVertexAttribPointer(aPositionHandle, 2, GLES20.GL_FLOAT, false, 8, fullQuadBuffer)

            GLES20.glEnableVertexAttribArray(aTexCoordHandle)
            GLES20.glVertexAttribPointer(aTexCoordHandle, 2, GLES20.GL_FLOAT, false, 8, primaryTexCoordBuffer)

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

            // Step 2: Draw secondary in PiP viewport if available
            if (secondaryTexId != null && pipWidth > 0 && pipHeight > 0) {
                // Sleek border around PiP window
                val border = 4
                val borderX = (pipX - border).coerceAtLeast(0)
                val borderY = (pipY - border).coerceAtLeast(0)
                val borderW = pipWidth + border * 2
                val borderH = pipHeight + border * 2

                GLES20.glEnable(GLES20.GL_SCISSOR_TEST)
                GLES20.glScissor(borderX, borderY, borderW, borderH)
                GLES20.glClearColor(0.12f, 0.16f, 0.24f, 1.0f)
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                GLES20.glDisable(GLES20.GL_SCISSOR_TEST)

                // Secondary frame inside PiP viewport with exact aspect ratio
                GLES20.glViewport(pipX, pipY, pipWidth, pipHeight)
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, secondaryTexId)
                GLES20.glUniformMatrix4fv(uSTMatrixHandle, 1, false, secondaryMatrix, 0)

                GLES20.glVertexAttribPointer(aPositionHandle, 2, GLES20.GL_FLOAT, false, 8, fullQuadBuffer)
                GLES20.glVertexAttribPointer(aTexCoordHandle, 2, GLES20.GL_FLOAT, false, 8, secondaryTexCoordBuffer)
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

                // Restore viewport
                GLES20.glViewport(0, 0, width, height)
            }

            GLES20.glDisableVertexAttribArray(aPositionHandle)
            GLES20.glDisableVertexAttribArray(aTexCoordHandle)
        }

        private fun loadShader(type: Int, shaderCode: String): Int {
            return GLES20.glCreateShader(type).also { shader ->
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
            }
        }

        fun release() {
            if (programId != 0) {
                GLES20.glDeleteProgram(programId)
                programId = 0
            }
            if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
                if (eglSurface != EGL14.EGL_NO_SURFACE) {
                    EGL14.eglDestroySurface(eglDisplay, eglSurface)
                    eglSurface = EGL14.EGL_NO_SURFACE
                }
                if (eglContext != EGL14.EGL_NO_CONTEXT) {
                    EGL14.eglDestroyContext(eglDisplay, eglContext)
                    eglContext = EGL14.EGL_NO_CONTEXT
                }
                EGL14.eglTerminate(eglDisplay)
                eglDisplay = EGL14.EGL_NO_DISPLAY
            }
        }
    }
}
