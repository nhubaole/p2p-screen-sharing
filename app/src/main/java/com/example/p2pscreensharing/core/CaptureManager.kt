package com.example.p2pscreensharing.core

import android.content.Context
import android.content.Intent

interface CaptureManager {
    fun initProjection(resultCode: Int, data: Intent, config: CaptureConfig)
    fun startCapturingFrames(onFrameCaptured: (ByteArray) -> Unit)
    fun stopCapturing()
}

data class CaptureConfig(val context: Context, val width: Int, val height: Int, val density: Int)