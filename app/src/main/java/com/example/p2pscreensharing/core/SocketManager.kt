package com.example.p2pscreensharing.core

interface SocketManager {
    suspend fun startServer(port: Int)
    suspend fun connectToHost(ip: String, port: Int)
    suspend fun send(data: ByteArray)
    suspend fun receive(): ByteArray?
    fun closeConnection()
}