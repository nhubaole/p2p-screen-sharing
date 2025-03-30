package com.example.p2pscreensharing.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.PeerEntity
import com.example.domain.usecase.CloseConnectionUseCase
import com.example.domain.usecase.StartReceivingUseCase
import com.example.domain.usecase.StartSocketServerUseCase
import com.example.domain.usecase.StopReceivingUseCase
import kotlinx.coroutines.launch

class ScreenViewingViewModel(
    private val startSocketServer: StartSocketServerUseCase,
    private val startReceiving: StartReceivingUseCase,
    private val stopReceiving: StopReceivingUseCase,
    private val closeConnection: CloseConnectionUseCase
) : ViewModel() {

    private val _frameResult = MutableLiveData<ByteArray>()
    val frameResult: LiveData<ByteArray> = _frameResult

    private val _peerEntity = MutableLiveData<PeerEntity?>()
    val peerEntity: LiveData<PeerEntity?> = _peerEntity

    fun startViewing(port: Int) {
        viewModelScope.launch {
            startSocketServer(
                port = port,
                onReady = {
                    _peerEntity.postValue(it)
                }
            )
            startReceiving { frame ->
                _frameResult.postValue(frame.payload)
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