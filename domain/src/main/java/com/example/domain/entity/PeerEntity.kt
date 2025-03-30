package com.example.domain.entity

enum class PeerRole {
    SENDER, RECEIVER
}

data class PeerEntity (
    val id: String?,
    val role: PeerRole,
    val ip: String,
    val port: Int
)