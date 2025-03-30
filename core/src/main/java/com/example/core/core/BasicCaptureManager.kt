package com.example.core.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.io.ByteArrayOutputStream

class BasicCaptureManager : CaptureManager {

    private lateinit var mediaProjection: MediaProjection
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    private var config: CaptureConfig? = null

    @Volatile
    private var isCapturing = false

    @SuppressLint("ServiceCast")
    override fun initProjection(resultCode: Int, data: Intent, config: CaptureConfig) {
        this.config = config
        val manager = config.context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = manager.getMediaProjection(resultCode, data)

        mediaProjection.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
            }
        }, Handler(Looper.getMainLooper()))

        imageReader = ImageReader.newInstance(config.width, config.height, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenSharing",
            config.width, config.height, config.density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )

        handlerThread = HandlerThread("CaptureThread").also { it.start() }
        handler = Handler(handlerThread!!.looper)
    }

    override fun startCapturingFrames(onFrameCaptured: (ByteArray) -> Unit) {
        isCapturing = true

        imageReader?.setOnImageAvailableListener({ reader ->
            if (!isCapturing) return@setOnImageAvailableListener

            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

            try {
                val jpegBytes = convertImageToByteArray(image)

                onFrameCaptured(jpegBytes)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                image.close()
            }

        }, handler)
    }

    override fun stopCapturing() {
        isCapturing = false

        imageReader?.setOnImageAvailableListener(null, null)

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
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)

        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, output)
        return output.toByteArray()
    }
}