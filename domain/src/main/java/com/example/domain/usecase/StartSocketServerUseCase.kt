package com.example.domain.usecase

import com.example.domain.entity.PeerEntity
import com.example.domain.repository.SignalingRepository

class StartSocketServerUseCase(
    private val signalingRepository: SignalingRepository
) {
    suspend operator fun invoke(port: Int, onReady: (PeerEntity?) -> Unit) {
        signalingRepository.startSocketServer(port, onReady)
    }
}