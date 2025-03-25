package com.example.p2pscreensharing.data.service

import com.example.p2pscreensharing.data.model.ClientInfo

interface SignalingService {
    suspend fun startSocketServer(port: Int, onReady: (ClientInfo?) -> Unit)
}