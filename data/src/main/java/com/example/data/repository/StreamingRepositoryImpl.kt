package com.example.data.repository

import com.example.data.service.StreamingService
import com.example.data.utils.toEntity
import com.example.domain.entity.FrameEntity
import com.example.domain.repository.StreamingRepository

class StreamingRepositoryImpl(
    private val streamingService: StreamingService
) : StreamingRepository {
    override fun startStreaming(ip: String?, port: Int?) {
        streamingService.startStreaming(ip, port)
    }

    override fun stopStreaming() {
        streamingService.stopStreaming()
    }

    override fun startReceiving(onFrameReceived: (FrameEntity) -> Unit) {
        streamingService.startReceiving { framePacket ->
            onFrameReceived(framePacket.toEntity())
        }
    }

    override fun stopReceiving() {
        streamingService.stopReceiving()
    }

    override fun closeConnection() {
        streamingService.closeConnection()
    }
}