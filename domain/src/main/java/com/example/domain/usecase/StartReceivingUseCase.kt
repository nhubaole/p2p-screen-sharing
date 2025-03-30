package com.example.domain.usecase

import com.example.domain.entity.FrameEntity
import com.example.domain.repository.StreamingRepository

class StartReceivingUseCase(
    private val repository: StreamingRepository
) {
    operator fun invoke(onFrameReceived: (FrameEntity) -> Unit) {
        repository.startReceiving(onFrameReceived)
    }
}