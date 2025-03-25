package com.example.p2pscreensharing.domain.repository

interface StreamingRepository {
    fun startStreaming(ip: String? = null, port: Int? = null)
    fun stopStreaming()
    fun startReceiving(onFrameReceived: (ByteArray) -> Unit)
    fun stopReceiving()
    fun closeConnection()
}