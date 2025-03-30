package com.example.data.service

import com.example.data.model.ClientInfo

interface SignalingService {
    suspend fun startSocketServer(port: Int, onReady: (ClientInfo?) -> Unit)
}