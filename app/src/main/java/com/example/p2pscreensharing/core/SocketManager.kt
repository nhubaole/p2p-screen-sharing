package com.example.p2pscreensharing.core

import com.example.p2pscreensharing.data.model.ClientInfo

interface SocketManager {
    suspend fun startServer(port: Int, onReady: (ClientInfo?) -> Unit)
    suspend fun connectToHost(ip: String, port: Int)
    suspend fun sendBytes(data: ByteArray)
    suspend fun receiveBytes(): ByteArray?
    fun closeConnection()
}