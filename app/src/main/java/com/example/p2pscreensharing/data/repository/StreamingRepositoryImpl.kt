package com.example.p2pscreensharing.data.repository

import com.example.p2pscreensharing.data.service.StreamingService
import com.example.p2pscreensharing.domain.repository.StreamingRepository

class StreamingRepositoryImpl(
    private val streamingService: StreamingService
) : StreamingRepository {
    override fun startStreaming() {
        streamingService.startStreaming()
    }

    override fun stopStreaming() {
        streamingService.stopStreaming()
    }

    override fun startReceiving(onFrameReceived: (ByteArray) -> Unit) {
        streamingService.startReceiving(onFrameReceived)
    }

    override fun stopReceiving() {
        streamingService.stopReceiving()
    }

    override fun closeConnection() {
        streamingService.closeConnection()
    }
}