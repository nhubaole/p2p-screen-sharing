package com.example.p2pscreensharing.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun startViewing(port: Int) {
        viewModelScope.launch {
            startSocketServer(port)
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