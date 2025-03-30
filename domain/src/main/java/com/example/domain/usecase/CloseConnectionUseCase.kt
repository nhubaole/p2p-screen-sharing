package com.example.domain.usecase

import com.example.domain.repository.StreamingRepository

class CloseConnectionUseCase(
    private val repository: StreamingRepository
) {
    operator fun invoke() {
        repository.closeConnection()
    }
}