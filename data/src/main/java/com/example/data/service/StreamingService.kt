package com.example.data.service

import com.example.data.model.FramePacket

interface StreamingService {
    fun startStreaming(ip: String? = null, port: Int? = null)

    fun stopStreaming()

    fun startReceiving(onFrameReceived: (FramePacket) -> Unit)

    fun stopReceiving()

    fun closeConnection()
}