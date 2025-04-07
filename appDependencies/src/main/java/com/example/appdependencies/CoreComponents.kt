package com.example.appdependencies

import com.example.core.core.BasicCaptureManager
import com.example.core.core.BasicVideoRecorder
import com.example.core.core.CaptureManager
import com.example.core.core.SocketManager
import com.example.core.core.UdpSocketManager
import com.example.core.core.VideoRecorder

object CoreComponents {
    private lateinit var socketManagerInstance: SocketManager
    private lateinit var captureManagerInstance: CaptureManager
    private lateinit var videoRecorderInstance: VideoRecorder

    fun getSocketManager(): SocketManager {
        if (!::socketManagerInstance.isInitialized) {
            socketManagerInstance = UdpSocketManager()
        }
        return socketManagerInstance
    }

    fun getCaptureManager(): CaptureManager {
        if (!::captureManagerInstance.isInitialized) {
            captureManagerInstance = BasicCaptureManager()
        }
        return captureManagerInstance
    }

    fun getVideoRecorder(): VideoRecorder {
        if (!::videoRecorderInstance.isInitialized) {
            videoRecorderInstance = BasicVideoRecorder()
        }
        return videoRecorderInstance
    }
}