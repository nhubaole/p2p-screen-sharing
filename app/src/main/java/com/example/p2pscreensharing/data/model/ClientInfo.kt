package com.example.p2pscreensharing.data.model

enum class PeerRole {
    SENDER, RECEIVER
}

data class ClientInfo(
    val id: String,
    val role: PeerRole,
    val ip: String,
    val port: Int
)