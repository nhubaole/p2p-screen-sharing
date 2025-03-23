package com.example.p2pscreensharing.domain.usecase

import com.example.p2pscreensharing.domain.repository.StreamingRepository

class StopStreamingUseCase(
    private val repository: StreamingRepository
) {
    operator fun invoke() {
        repository.stopStreaming()
    }
}