package com.example.p2pscreensharing.domain.repository

import com.example.p2pscreensharing.data.model.ClientInfo

interface SignalingRepository {
    suspend fun startSocketServer(port: Int, onReady: (ClientInfo?) -> Unit)
}