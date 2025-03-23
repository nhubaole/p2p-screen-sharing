package com.example.p2pscreensharing.domain.usecase

import com.example.p2pscreensharing.domain.repository.SignalingRepository

class StartSocketServerUseCase(
    private val signalingRepository: SignalingRepository
) {
    suspend operator fun invoke(port: Int) {
        signalingRepository.startSocketServer(port)
    }
}