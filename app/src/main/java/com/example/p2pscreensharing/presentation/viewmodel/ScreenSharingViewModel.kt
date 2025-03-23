package com.example.p2pscreensharing.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2pscreensharing.domain.usecase.CloseConnectionUseCase
import com.example.p2pscreensharing.domain.usecase.ConnectToPeerUseCase
import com.example.p2pscreensharing.domain.usecase.StartStreamingUseCase
import com.example.p2pscreensharing.domain.usecase.StopStreamingUseCase
import kotlinx.coroutines.launch

class ScreenSharingViewModel(
    private val connectToPeer: ConnectToPeerUseCase,
    private val startStreaming: StartStreamingUseCase,
    private val stopStreaming: StopStreamingUseCase,
    private val closeConnection: CloseConnectionUseCase
) : ViewModel() {

    fun connectAndStartSharing(ip: String, port: Int) {
        viewModelScope.launch {
            connectToPeer(ip, port)
            startStreaming()
        }
    }

    fun stopSharing() {
        viewModelScope.launch {
            stopStreaming()
            closeConnection()
        }
    }

    override fun onCleared() {
        stopSharing()
        super.onCleared()
    }
}