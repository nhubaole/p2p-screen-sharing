package com.example.domain.repository

import com.example.domain.entity.PeerEntity

interface SignalingRepository {
    suspend fun startSocketServer(port: Int, onReady: (PeerEntity?) -> Unit)

    suspend fun startTcpSocketServer(
        port: Int,
        onReady: (PeerEntity?) -> Unit,
        onClientConnected: () -> Unit
    )

    suspend fun connectToPeer(ip: String, port: Int)

    fun closeConnection()

    fun closeTcpConnection()
}