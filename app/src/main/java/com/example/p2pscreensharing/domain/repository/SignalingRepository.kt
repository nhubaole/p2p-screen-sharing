package com.example.p2pscreensharing.domain.repository

interface SignalingRepository {
    suspend fun startSocketServer(port: Int)
    suspend fun connectToPeer(ip: String, port: Int)
}