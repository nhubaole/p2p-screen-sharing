package com.example.appdependencies

import com.example.core.core.BasicCaptureManager
import com.example.core.core.CaptureManager
import com.example.core.core.SocketManager
import com.example.core.core.UdpSocketManager

object CoreComponents {
    private lateinit var socketManagerInstance: SocketManager
    private lateinit var captureManagerInstance: CaptureManager

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
}