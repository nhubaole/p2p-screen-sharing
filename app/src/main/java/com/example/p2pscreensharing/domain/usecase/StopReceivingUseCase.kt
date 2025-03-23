package com.example.p2pscreensharing.domain.usecase

import com.example.p2pscreensharing.domain.repository.StreamingRepository

class StopReceivingUseCase(
    private val repository: StreamingRepository
) {
    operator fun invoke() {
        repository.stopReceiving()
    }
}