package com.example.p2pscreensharing.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.CloseTcpConnectionUseCase
import com.example.domain.usecase.ConnectToPeerUseCase
import com.example.domain.usecase.StartTcpSocketServerUseCase
import com.example.p2pscreensharing.presentation.uistate.ConnectToPeerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val startTcpSocketServer: StartTcpSocketServerUseCase,
    private val connectToPeer: ConnectToPeerUseCase,
    private val closeTcpConnection: CloseTcpConnectionUseCase,
) : ViewModel() {
    private val PORT = 8080

    private val _connectToPeerUiState = MutableStateFlow(ConnectToPeerUiState())
    val uiState: StateFlow<ConnectToPeerUiState> = _connectToPeerUiState

    private val _peerConnectedResult = MutableLiveData<String>()
    val peerConnectedResult: LiveData<String> = _peerConnectedResult

    private var serverJob: Job? = null
    private var timerJob: Job? = null

    fun initData() {
        startServer()
    }

    private fun startServer() {
        serverJob?.cancel()
        serverJob = viewModelScope.launch {
            startTcpSocketServer.invoke(
                port = PORT,
                onReady = { connection ->
                    _connectToPeerUiState.value = _connectToPeerUiState.value.copy(
                        isConnected = false,
                        isServerMode = true
                    )
                },
                onClientConnected = {
                    _connectToPeerUiState.value = _connectToPeerUiState.value.copy(
                        isConnected = true,
                        isServerMode = true
                    )

                    startTimer()

                    _peerConnectedResult.postValue("")
                }
            )
        }
    }

    fun connectToPeer(
        peerIp: String
    ) {
        viewModelScope.launch {
            serverJob?.cancel()
            closeTcpConnection()

            connectToPeer.invoke(peerIp, PORT)
            _connectToPeerUiState.value = _connectToPeerUiState.value.copy(
                isConnected = true,
                isServerMode = false
            )

            startTimer()

            _peerConnectedResult.postValue(peerIp)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            closeTcpConnection()

            timerJob?.cancel()

            _connectToPeerUiState.value = _connectToPeerUiState.value.copy(
                isConnected = false,
                time = "00:00:00"
            )

            startServer()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val seconds = elapsed / 1000
                val formatted = String.format(
                    "%02d:%02d:%02d",
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60
                )
                updateConnectionTime(formatted)
                delay(1000)
            }
        }
    }

    fun updateIpInfo(ip: String) {
        _connectToPeerUiState.value = _connectToPeerUiState.value.copy(ip = ip)
    }

    fun updateConnectionTime(time: String) {
        _connectToPeerUiState.value = _connectToPeerUiState.value.copy(time = time)
    }

    fun updateConnectionStatus(isConnected: Boolean) {
        _connectToPeerUiState.value = _connectToPeerUiState.value.copy(isConnected = isConnected)
    }
}