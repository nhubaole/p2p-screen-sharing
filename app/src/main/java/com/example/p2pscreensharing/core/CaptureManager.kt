package com.example.p2pscreensharing.core

interface CaptureManager {
    fun startCapturingFrames(onFrameCaptured: (ByteArray) -> Unit)
    fun stopCapturing()
}