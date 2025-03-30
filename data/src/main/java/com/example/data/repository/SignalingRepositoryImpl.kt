package com.example.data.repository

import com.example.data.service.SignalingService
import com.example.data.utils.toEntity
import com.example.domain.entity.PeerEntity
import com.example.domain.repository.SignalingRepository

class SignalingRepositoryImpl(
    private val signalingService: SignalingService
) : SignalingRepository {
    override suspend fun startSocketServer(port: Int, onReady: (PeerEntity?) -> Unit) {
        signalingService.startSocketServer(port = port, onReady = {
            onReady(it?.toEntity())
        })
    }
}