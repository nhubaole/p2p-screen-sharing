package com.example.domain.usecase

import com.example.domain.repository.SignalingRepository

class CloseTcpConnectionUseCase (
    private val repository: SignalingRepository
) {
    operator fun invoke() {
        repository.closeTcpConnection()
    }
}