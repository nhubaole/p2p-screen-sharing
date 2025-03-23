package com.example.p2pscreensharing.data.service

interface SignalingService {
    suspend fun startSocketServer(port: Int)
    suspend fun connectToPeer(ip: String, port: Int)
}