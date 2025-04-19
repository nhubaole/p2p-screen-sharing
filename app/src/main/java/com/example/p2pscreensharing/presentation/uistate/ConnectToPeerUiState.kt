package com.example.p2pscreensharing.presentation.uistate

data class ConnectToPeerUiState (
    val ip: String = "",
    val isConnected: Boolean = false,
    val time: String = ""
)