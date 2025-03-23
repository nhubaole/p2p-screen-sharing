package com.example.p2pscreensharing.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2pscreensharing.data.model.ClientInfo
import com.example.p2pscreensharing.domain.usecase.CloseConnectionUseCase
import com.example.p2pscreensharing.domain.usecase.StartReceivingUseCase
import com.example.p2pscreensharing.domain.usecase.StartSocketServerUseCase
import com.example.p2pscreensharing.domain.usecase.StopReceivingUseCase
import kotlinx.coroutines.launch

class ScreenViewingViewModel(
    private val startSocketServer: StartSocketServerUseCase,
    private val startReceiving: StartReceivingUseCase,
    private val stopReceiving: StopReceivingUseCase,
    private val closeConnection: CloseConnectionUseCase
) : ViewModel() {

    private val _frameResult = MutableLiveData<ByteArray>()
    val frameResult: LiveData<ByteArray> = _frameResult

    private val _clientInfo = MutableLiveData<ClientInfo?>()
    val clientInfo: LiveData<ClientInfo?> = _clientInfo

    fun startViewing(port: Int) {
        viewModelScope.launch {
            startSocketServer(
                port = port,
                onReady = {
                    _clientInfo.postValue(it)
                }
            )
            startReceiving { frame ->
                _frameResult.postValue(frame)
            }
        }
    }

    fun stopViewing() {
        viewModelScope.launch {
            stopReceiving()
            closeConnection()
        }
    }

    override fun onCleared() {
        stopViewing()
        super.onCleared()
    }
}