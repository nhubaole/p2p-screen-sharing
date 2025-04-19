package com.example.appdependencies

import com.example.core.core.BasicCaptureManager
import com.example.core.core.BasicTcpSocketManager
import com.example.core.core.BasicVideoRecorder
import com.example.core.core.CaptureManager
import com.example.core.core.UdpSocketManager
import com.example.core.core.BasicUdpSocketManager
import com.example.core.core.TcpSocketManager
import com.example.core.core.VideoRecorder

object CoreComponents {
    private lateinit var udpSocketManagerInstance: UdpSocketManager
    private lateinit var tcpSocketManagerInstance: TcpSocketManager
    private lateinit var captureManagerInstance: CaptureManager
    private lateinit var videoRecorderInstance: VideoRecorder

    fun getSocketManager(): UdpSocketManager {
        if (!::udpSocketManagerInstance.isInitialized) {
            udpSocketManagerInstance = BasicUdpSocketManager()
        }
        return udpSocketManagerInstance
    }

    fun getTcpSocketManager(): TcpSocketManager {
        if (!::tcpSocketManagerInstance.isInitialized) {
            tcpSocketManagerInstance = BasicTcpSocketManager()
        }
        return tcpSocketManagerInstance
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