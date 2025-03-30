package com.example.domain.usecase

import com.example.domain.repository.StreamingRepository

class StopStreamingUseCase(
    private val repository: StreamingRepository
) {
    operator fun invoke() {
        repository.stopStreaming()
    }
}