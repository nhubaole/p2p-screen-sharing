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

    override suspend fun startTcpSocketServer(
        port: Int,
        onReady: (PeerEntity?) -> Unit,
        onClientConnected: () -> Unit
    ) {
        signalingService.startTcpSocketServer(
            port = port,
            onReady = {
                onReady(it?.toEntity())
            },
            onClientConnected = onClientConnected
        )
    }

    override suspend fun connectToPeer(ip: String, port: Int) {
        signalingService.connectToPeer(ip = ip, port = port)
    }

    override fun closeConnection() {
        signalingService.closeConnection()
    }

    override fun closeTcpConnection() {
        signalingService.closeTcpConnection()
    }
}