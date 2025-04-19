package com.example.core.core

interface TcpSocketManager {
    suspend fun startServer(port: Int, onReady: (ip: String?, port: Int?) -> Unit)

    suspend fun connectToHost(ip: String, port: Int)

    suspend fun sendBytes(data: ByteArray)

    suspend fun receiveBytes(): ByteArray?

    fun closeConnection()
}