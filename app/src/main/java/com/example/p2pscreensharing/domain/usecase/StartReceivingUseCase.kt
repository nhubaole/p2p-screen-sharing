package com.example.p2pscreensharing.domain.usecase

import com.example.p2pscreensharing.domain.repository.StreamingRepository

class StartReceivingUseCase(
    private val repository: StreamingRepository
) {
    operator fun invoke(onFrameReceived: (ByteArray) -> Unit) {
        repository.startReceiving(onFrameReceived)
    }
}