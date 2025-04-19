package com.example.data.service

import com.example.data.model.ClientInfo

interface SignalingService {
    suspend fun startSocketServer(port: Int, onReady: (ClientInfo?) -> Unit)

    suspend fun startTcpSocketServer(port: Int, onReady: (ClientInfo?) -> Unit)

    suspend fun connectToPeer(ip: String, port: Int)

    fun closeConnection()

    fun closeTcpConnection()
}