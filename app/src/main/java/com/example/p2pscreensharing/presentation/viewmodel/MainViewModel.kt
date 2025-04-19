package com.example.p2pscreensharing.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.domain.usecase.CloseTcpConnectionUseCase
import com.example.domain.usecase.StartTcpSocketServerUseCase
import com.example.p2pscreensharing.presentation.uistate.ConnectToPeerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(
    private val startTcpSocketServer: StartTcpSocketServerUseCase,
    private val closeTcpConnection: CloseTcpConnectionUseCase,
) : ViewModel() {
    private val _connectToPeerUiState = MutableStateFlow(ConnectToPeerUiState())
    val uiState: StateFlow<ConnectToPeerUiState> = _connectToPeerUiState

    fun initData() {

    }

    fun updateIpInfo(ip: String) {
        _connectToPeerUiState.value = _connectToPeerUiState.value.copy(ip = ip)
    }

    fun updateConnectionStatus(isConnected: Boolean) {
        _connectToPeerUiState.value = _connectToPeerUiState.value.copy(isConnected = isConnected)
    }

    fun updateConnectionTime(time: String) {
        _connectToPeerUiState.value = _connectToPeerUiState.value.copy(time = time)

    }
}