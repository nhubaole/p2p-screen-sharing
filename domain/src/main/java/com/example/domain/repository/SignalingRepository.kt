package com.example.domain.repository

import com.example.domain.entity.PeerEntity

interface SignalingRepository {
    suspend fun startSocketServer(port: Int, onReady: (PeerEntity?) -> Unit)
}