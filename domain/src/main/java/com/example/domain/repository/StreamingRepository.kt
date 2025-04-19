package com.example.domain.repository

import com.example.domain.entity.FrameEntity

interface StreamingRepository {
    fun startStreaming(ip: String? = null, port: Int? = null)

    fun stopStreaming()

    fun startReceiving(onFrameReceived: (FrameEntity) -> Unit)

    fun stopReceiving()
}