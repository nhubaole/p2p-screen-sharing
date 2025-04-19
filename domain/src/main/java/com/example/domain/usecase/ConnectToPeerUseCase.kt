package com.example.domain.usecase

import com.example.domain.entity.PeerEntity
import com.example.domain.repository.SignalingRepository

class ConnectToPeerUseCase (
    private val signalingRepository: SignalingRepository
) {
    suspend operator fun invoke(ip: String, port: Int) {
        signalingRepository.connectToPeer(ip, port)
    }
}