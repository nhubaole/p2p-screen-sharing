package com.example.p2pscreensharing

import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.core.core.BasicVideoRecorder
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class VideoRecorderTest {

    @Test
    fun testVideoEncodingToDownloads() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        if (Build.VERSION.SDK_INT in 23..28) {
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                InstrumentationRegistry.getInstrumentation().uiAutomation
                    .executeShellCommand("pm grant ${context.packageName} $permission")
            }
        }

        val width = 480
        val height = 800
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "unit_test_record.mp4")

        val recorder = BasicVideoRecorder()
        recorder.start(width, height, file)

        val bitmap = createBitmap(width, height)

        repeat(30) {
            recorder.encodeFrame(bitmap)
            Thread.sleep(33)
        }

        recorder.stop()

        assertTrue("Output file should exist", file.exists() && file.length() > 0)
    }

    private fun createBitmap(w: Int, h: Int): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint().apply { color = Color.MAGENTA }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        return bmp
    }
}