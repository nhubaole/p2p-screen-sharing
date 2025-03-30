package com.example.domain.usecase

import com.example.domain.repository.StreamingRepository

class StopReceivingUseCase(
    private val repository: StreamingRepository
) {
    operator fun invoke() {
        repository.stopReceiving()
    }
}