package com.example.core.core

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class BasicVideoRecoder : VideoRecorder {

    private var mediaCodec: MediaCodec? = null

    private var mediaMuxer: MediaMuxer? = null

    private var trackIndex = -1

    private var isMuxerStarted = false

    private var isRecording = false

    private var presentationTimeUs = 0L

    private var width: Int = 0
    private var height: Int = 0

    private val recordingScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun start(width: Int, height: Int, outputFile: File) {
        this.width = width
        this.height = height

        val format = MediaFormat.createVideoFormat("video/avc", width, height).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
            setInteger(MediaFormat.KEY_BIT_RATE, 2_000_000)
            setInteger(MediaFormat.KEY_FRAME_RATE, 15)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        }

        mediaCodec = MediaCodec.createEncoderByType("video/avc").apply {
            configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            start()
        }

        mediaMuxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        isRecording = true
    }

    override fun encodeFrame(bitmap: Bitmap) {
        if (!isRecording) return

        recordingScope.launch {
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            val yuv = bitmapToNV21(resizedBitmap)
            encodeYUVToVideo(yuv)
        }
    }

    override fun stop() {
        isRecording = false
        recordingScope.cancel()

        mediaCodec?.run {
            stop()
            release()
        }
        mediaCodec = null

        if (isMuxerStarted) {
            mediaMuxer?.run {
                stop()
                release()
            }
        }
        mediaMuxer = null
        isMuxerStarted = false
    }

    private fun encodeYUVToVideo(yuv: ByteArray) {
        val codec = mediaCodec ?: return
        if (!isRecording) return

        val inputBufferIndex = codec.dequeueInputBuffer(10_000L)
        if (inputBufferIndex >= 0) {
            val inputBuffer = codec.getInputBuffer(inputBufferIndex) ?: return
            inputBuffer.clear()
            inputBuffer.put(yuv)
            presentationTimeUs += 1_000_000L / 15
            codec.queueInputBuffer(inputBufferIndex, 0, yuv.size, presentationTimeUs, 0)
        }

        val bufferInfo = MediaCodec.BufferInfo()
        while (true) {
            val outputIndex = codec.dequeueOutputBuffer(bufferInfo, 0)
            when {
                outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    if (!isMuxerStarted) {
                        val newFormat = codec.outputFormat
                        trackIndex = mediaMuxer!!.addTrack(newFormat)
                        mediaMuxer!!.start()
                        isMuxerStarted = true
                    }
                }

                outputIndex >= 0 -> {
                    val encodedData = codec.getOutputBuffer(outputIndex) ?: continue
                    if (bufferInfo.size > 0 && isMuxerStarted) {
                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        mediaMuxer?.writeSampleData(trackIndex, encodedData, bufferInfo)
                    }
                    codec.releaseOutputBuffer(outputIndex, false)
                }

                else -> break
            }
        }
    }

    private fun bitmapToNV21(bitmap: Bitmap): ByteArray {
        val argb = IntArray(width * height)
        bitmap.getPixels(argb, 0, width, 0, 0, width, height)
        val yuv = ByteArray(width * height * 3 / 2)

        var yIndex = 0
        var uvIndex = width * height

        for (j in 0 until height) {
            for (i in 0 until width) {
                val index = j * width + i
                val color = argb[index]

                val r = (color shr 16) and 0xFF
                val g = (color shr 8) and 0xFF
                val b = color and 0xFF

                val y = ((66 * r + 129 * g + 25 * b + 128) shr 8) + 16
                val u = ((-38 * r - 74 * g + 112 * b + 128) shr 8) + 128
                val v = ((112 * r - 94 * g - 18 + 128) shr 8) + 128

                yuv[yIndex++] = y.coerceIn(0, 255).toByte()
                if (j % 2 == 0 && i % 2 == 0) {
                    yuv[uvIndex++] = v.coerceIn(0, 255).toByte()
                    yuv[uvIndex++] = u.coerceIn(0, 255).toByte()
                }
            }
        }

        return yuv
    }
}