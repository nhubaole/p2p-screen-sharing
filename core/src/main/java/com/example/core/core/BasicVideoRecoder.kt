package com.example.core.core

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import java.io.File

class BasicVideoRecoder(
    private val width: Int,
    private val height: Int,
    private val outputFile: File
) : VideoRecorder {

    private var mediaCodec: MediaCodec? = null

    private var mediaMuxer: MediaMuxer? = null

    private var trackIndex = -1

    private var isMuxerStarted = false

    private var presentationTimeUs = 0L

    private var isRecording = false

    override fun start() {
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

        val inputBufferIndex = mediaCodec?.dequeueInputBuffer(10000L) ?: return
        if (inputBufferIndex >= 0) {
            val inputBuffer = mediaCodec?.getInputBuffer(inputBufferIndex)
            inputBuffer?.clear()

            val yuv = bitmapToYUV420(bitmap)
            inputBuffer?.put(yuv)

            presentationTimeUs += 66_666
            mediaCodec?.queueInputBuffer(inputBufferIndex, 0, yuv.size, presentationTimeUs, 0)
        }

        val bufferInfo = MediaCodec.BufferInfo()
        while (true) {
            val outputIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 0) ?: break
            if (outputIndex >= 0) {
                val encodedData = mediaCodec?.getOutputBuffer(outputIndex) ?: continue
                if (bufferInfo.size != 0) {
                    encodedData.position(bufferInfo.offset)
                    encodedData.limit(bufferInfo.offset + bufferInfo.size)

                    if (!isMuxerStarted) {
                        trackIndex = mediaCodec?.outputFormat?.let {
                            mediaMuxer?.addTrack(it)
                        } ?: -1
                        mediaMuxer?.start()
                        isMuxerStarted = true
                    }

                    mediaMuxer?.writeSampleData(trackIndex, encodedData, bufferInfo)
                }
                mediaCodec?.releaseOutputBuffer(outputIndex, false)
            } else {
                break
            }
        }
    }

    override fun stop() {
        isRecording = false
        mediaCodec?.stop()
        mediaCodec?.release()
        if (isMuxerStarted) {
            mediaMuxer?.stop()
            mediaMuxer?.release()
        }
    }

    private fun bitmapToYUV420(bitmap: Bitmap): ByteArray {
        val argb = IntArray(width * height)
        bitmap.getPixels(argb, 0, width, 0, 0, width, height)
        val yuv = ByteArray(width * height * 3 / 2)

        var yIndex = 0
        var uIndex = width * height
        var vIndex = uIndex + (uIndex / 4)

        for (j in 0 until height) {
            for (i in 0 until width) {
                val rgb = argb[j * width + i]
                val r = (rgb shr 16) and 0xFF
                val g = (rgb shr 8) and 0xFF
                val b = rgb and 0xFF

                val y = ((66 * r + 129 * g + 25 * b + 128) shr 8) + 16
                val u = ((-38 * r - 74 * g + 112 * b + 128) shr 8) + 128
                val v = ((112 * r - 94 * g - 18 * b + 128) shr 8) + 128

                yuv[yIndex++] = y.coerceIn(0, 255).toByte()
                if (j % 2 == 0 && i % 2 == 0) {
                    yuv[uIndex++] = u.coerceIn(0, 255).toByte()
                    yuv[vIndex++] = v.coerceIn(0, 255).toByte()
                }
            }
        }

        return yuv
    }
}