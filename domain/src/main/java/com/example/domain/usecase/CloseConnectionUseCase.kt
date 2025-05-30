package com.example.domain.usecase

import com.example.domain.repository.SignalingRepository

class CloseConnectionUseCase(
    private val repository: SignalingRepository
) {
    operator fun invoke() {
        repository.closeConnection()
    }
}