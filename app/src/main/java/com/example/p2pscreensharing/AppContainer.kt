package com.example.p2pscreensharing.di

import com.example.appdependencies.DomainComponents.createCloseConnectionUseCase
import com.example.appdependencies.DomainComponents.createCloseTcpConnectionUseCase
import com.example.appdependencies.DomainComponents.createConnectToPeerUseCase
import com.example.appdependencies.DomainComponents.createStartReceivingUseCase
import com.example.appdependencies.DomainComponents.createStartSocketServerUseCase
import com.example.appdependencies.DomainComponents.createStartTcpSocketServerUseCase
import com.example.appdependencies.DomainComponents.createStopReceivingUseCase
import com.example.p2pscreensharing.presentation.viewmodel.MainViewModel
import com.example.p2pscreensharing.presentation.viewmodel.ScreenViewingViewModel

object AppContainer {

    fun createMainViewModel() : MainViewModel {
        return MainViewModel(
            startTcpSocketServer = createStartTcpSocketServerUseCase(),
            connectToPeer = createConnectToPeerUseCase(),
            closeTcpConnection = createCloseTcpConnectionUseCase()
        )
    }

    fun createScreenViewingViewModel(): ScreenViewingViewModel {
        return ScreenViewingViewModel(
            startSocketServer = createStartSocketServerUseCase(),
            startReceiving = createStartReceivingUseCase(),
            stopReceiving = createStopReceivingUseCase(),
            closeConnection = createCloseConnectionUseCase()
        )
    }

}