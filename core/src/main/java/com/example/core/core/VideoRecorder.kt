package com.example.core.core

import android.graphics.Bitmap
import java.io.File

interface VideoRecorder {
    fun start(
        width: Int,
        height: Int,
        outputFile: File
    )

    fun encodeFrame(bitmap: Bitmap)

    fun stop()
}