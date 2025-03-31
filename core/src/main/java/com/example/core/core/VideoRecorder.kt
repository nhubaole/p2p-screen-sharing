package com.example.core.core

import android.graphics.Bitmap

interface VideoRecorder {
    fun start()

    fun encodeFrame(bitmap: Bitmap)

    fun stop()
}