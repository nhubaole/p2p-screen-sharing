package com.example.p2pscreensharing.data.repository

import com.example.p2pscreensharing.data.service.SignalingService
import com.example.p2pscreensharing.domain.repository.SignalingRepository

class SignalingRepositoryImpl(
    private val signalingService: SignalingService
) : SignalingRepository {

    override suspend fun startSocketServer(port: Int) {
        signalingService.startSocketServer(port)
    }

    override suspend fun connectToPeer(ip: String, port: Int) {
        signalingService.connectToPeer(ip, port)
    }
}