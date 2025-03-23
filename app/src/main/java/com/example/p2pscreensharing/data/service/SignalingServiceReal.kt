package com.example.p2pscreensharing.data.service

import com.example.p2pscreensharing.core.SocketManager

class SignalingServiceReal(
    private val socketManager: SocketManager
) : SignalingService {

    override suspend fun startSocketServer(port: Int) {
        socketManager.startServer(port)
    }

    override suspend fun connectToPeer(ip: String, port: Int) {
        socketManager.connectToHost(ip, port)
    }
}