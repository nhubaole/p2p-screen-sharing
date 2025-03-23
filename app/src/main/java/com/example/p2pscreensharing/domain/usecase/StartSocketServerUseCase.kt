package com.example.p2pscreensharing.domain.usecase

import com.example.p2pscreensharing.data.model.ClientInfo
import com.example.p2pscreensharing.domain.repository.SignalingRepository

class StartSocketServerUseCase(
    private val signalingRepository: SignalingRepository
) {
    suspend operator fun invoke(port: Int, onReady: (ClientInfo?) -> Unit) {
        signalingRepository.startSocketServer(port, onReady)
    }
}