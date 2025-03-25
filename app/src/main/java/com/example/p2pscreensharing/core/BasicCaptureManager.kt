package com.example.p2pscreensharing.core

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import com.example.p2pscreensharing.data.model.FramePacket
import java.io.ByteArrayOutputStream

class BasicCaptureManager(
    private val mediaProjection: MediaProjection,
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val screenDensity: Int
) : CaptureManager {

    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null

    override fun startCapturingFrames(onFrameCaptured: (ByteArray) -> Unit) {
        handlerThread = HandlerThread("ScreenCaptureThread").also { it.start() }
        handler = Handler(handlerThread!!.looper)

        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            screenWidth,
            screenHeight,
            screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null,
            handler
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            val jpegBytes = convertImageToByteArray(image)
            image.close()

            val packet = FramePacket(
                timestamp = System.currentTimeMillis(),
                width = screenWidth,
                height = screenHeight,
                payload = jpegBytes
            )
            val encoded = FramePacket.encode(packet)

            onFrameCaptured(encoded)
        }, handler)
    }

    override fun stopCapturing() {
        imageReader?.close()
        virtualDisplay?.release()
        handlerThread?.quitSafely()
        mediaProjection.stop()
    }

    private fun convertImageToByteArray(image: Image): ByteArray {
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * screenWidth

        val bitmap = Bitmap.createBitmap(
            screenWidth + rowPadding / pixelStride,
            screenHeight,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        return outputStream.toByteArray()
    }
}