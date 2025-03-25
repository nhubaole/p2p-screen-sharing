package com.example.p2pscreensharing.data.repository

import com.example.p2pscreensharing.data.model.ClientInfo
import com.example.p2pscreensharing.data.service.SignalingService
import com.example.p2pscreensharing.domain.repository.SignalingRepository

class SignalingRepositoryImpl(
    private val signalingService: SignalingService
) : SignalingRepository {

    override suspend fun startSocketServer(port: Int, onReady: (ClientInfo?) -> Unit) {
        signalingService.startSocketServer(port, onReady)
    }
}