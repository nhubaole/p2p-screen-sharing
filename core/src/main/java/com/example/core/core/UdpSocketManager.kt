package com.example.core.core

interface UdpSocketManager {
    suspend fun startServer(port: Int, onReady: (ip: String?, port: Int?) -> Unit)

    suspend fun sendBytes(data: ByteArray, ip: String? = null, port: Int? = null)

    suspend fun receiveBytes(): ByteArray?

    fun closeConnection()
}