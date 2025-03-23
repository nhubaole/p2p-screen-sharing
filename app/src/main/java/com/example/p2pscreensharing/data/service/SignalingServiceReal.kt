package com.example.p2pscreensharing.data.service

import com.example.p2pscreensharing.core.SocketManager
import com.example.p2pscreensharing.data.model.ClientInfo

class SignalingServiceReal(
    private val socketManager: SocketManager
) : SignalingService {

    override suspend fun startSocketServer(port: Int, onReady: (ClientInfo?) -> Unit) {
        socketManager.startServer(port, onReady)
    }

    override suspend fun connectToPeer(ip: String, port: Int) {
        socketManager.connectToHost(ip, port)
    }
}