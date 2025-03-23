package com.example.p2pscreensharing.domain.usecase

import com.example.p2pscreensharing.domain.repository.SignalingRepository

class ConnectToPeerUseCase(
    private val signalingRepository: SignalingRepository
) {
    suspend operator fun invoke(ip: String, port: Int) {
        signalingRepository.connectToPeer(ip, port)
    }
}