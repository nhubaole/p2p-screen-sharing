package com.example.domain.usecase

import com.example.domain.repository.StreamingRepository

class StartStreamingUseCase(
    private val repository: StreamingRepository
) {
    operator fun invoke(ip: String? = null, port: Int? = null) {
        repository.startStreaming(ip, port)
    }
}