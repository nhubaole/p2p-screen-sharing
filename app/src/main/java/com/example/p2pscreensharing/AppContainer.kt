package com.example.p2pscreensharing.di

import com.example.appdependencies.DomainComponents.createCloseConnectionUseCase
import com.example.appdependencies.DomainComponents.createStartReceivingUseCase
import com.example.appdependencies.DomainComponents.createStartSocketServerUseCase
import com.example.appdependencies.DomainComponents.createStopReceivingUseCase
import com.example.domain.usecase.CloseConnectionUseCase
import com.example.domain.usecase.StartSocketServerUseCase
import com.example.p2pscreensharing.presentation.viewmodel.ScreenViewingViewModel

object AppContainer {

    fun createScreenViewingViewModel(): ScreenViewingViewModel {
        return ScreenViewingViewModel(
            startSocketServer = createStartSocketServerUseCase(),
            startReceiving = createStartReceivingUseCase(),
            stopReceiving = createStopReceivingUseCase(),
            closeConnection = createCloseConnectionUseCase()
        )
    }

}