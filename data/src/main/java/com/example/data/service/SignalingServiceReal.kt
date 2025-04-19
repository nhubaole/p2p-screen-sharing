package com.example.data.service

import com.example.core.core.TcpSocketManager
import com.example.core.core.UdpSocketManager
import com.example.data.model.ClientInfo

class SignalingServiceReal(
    private val udpSocketManager: UdpSocketManager,
    private val tcpSocketManager: TcpSocketManager
) : SignalingService {

    override suspend fun startSocketServer(port: Int, onReady: (ClientInfo?) -> Unit) {
        udpSocketManager.startServer(port = port, onReady = { ip, port ->
            onReady(
                ClientInfo(
                    ip = ip.orEmpty(),
                    port = port ?: 0
                )
            )
        })
    }

    override suspend fun startTcpSocketServer(port: Int, onReady: (ClientInfo?) -> Unit) {
        tcpSocketManager.startServer(port = port) { ip, port ->
            onReady(
                ClientInfo(
                    ip = ip.orEmpty(),
                    port = port ?: 0
                )
            )
        }
    }

    override suspend fun connectToPeer(ip: String, port: Int) {
        tcpSocketManager.connectToHost(ip = ip, port = port)
    }

    override fun closeConnection() {
        udpSocketManager.closeConnection()
    }

    override fun closeTcpConnection() {
        tcpSocketManager.closeConnection()
    }
}