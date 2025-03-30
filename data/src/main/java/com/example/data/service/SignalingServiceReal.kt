package com.example.data.service

import com.example.core.core.SocketManager
import com.example.data.model.ClientInfo

class SignalingServiceReal(
    private val socketManager: SocketManager
) : SignalingService {

    override suspend fun startSocketServer(port: Int, onReady: (ClientInfo?) -> Unit) {
        socketManager.startServer(port = port, onReady = { ip, port ->
            onReady(
                ClientInfo(
                    ip = ip.orEmpty(),
                    port = port ?: 0
                )
            )
        })
    }
}