package com.example.appdependencies

import com.example.appdependencies.CoreComponents.getCaptureManager
import com.example.appdependencies.CoreComponents.getSocketManager
import com.example.data.repository.SignalingRepositoryImpl
import com.example.data.repository.StreamingRepositoryImpl
import com.example.data.service.SignalingService
import com.example.data.service.SignalingServiceReal
import com.example.data.service.StreamingService
import com.example.data.service.StreamingServiceReal
import com.example.domain.repository.SignalingRepository
import com.example.domain.repository.StreamingRepository
import com.example.domain.usecase.CloseConnectionUseCase
import com.example.domain.usecase.StartReceivingUseCase
import com.example.domain.usecase.StartSocketServerUseCase
import com.example.domain.usecase.StartStreamingUseCase
import com.example.domain.usecase.StopReceivingUseCase
import com.example.domain.usecase.StopStreamingUseCase

object DomainComponents {

    private lateinit var signalingServiceInstance: SignalingService

    private lateinit var streamingServiceInstance: StreamingService

    fun getSignalingService(): SignalingService {
        if (!::signalingServiceInstance.isInitialized) {
            signalingServiceInstance =
                SignalingServiceReal(getSocketManager())
        }
        return signalingServiceInstance
    }

    fun getStreamingService(): StreamingService {
        if (!::streamingServiceInstance.isInitialized) {
            streamingServiceInstance =
                StreamingServiceReal(getSocketManager(), getCaptureManager())
        }
        return streamingServiceInstance
    }

    fun createStreamingRepository(): StreamingRepository {
        return StreamingRepositoryImpl(getStreamingService())
    }

    fun createSignalingRepository(): SignalingRepository {
        return SignalingRepositoryImpl(getSignalingService())
    }

    fun createStartStreamingUseCase(): StartStreamingUseCase {
        return StartStreamingUseCase(createStreamingRepository())
    }

    fun createStopStreamingUseCase(): StopStreamingUseCase {
        return StopStreamingUseCase(createStreamingRepository())
    }

    fun createStartReceivingUseCase(): StartReceivingUseCase {
        return StartReceivingUseCase(createStreamingRepository())
    }

    fun createStopReceivingUseCase(): StopReceivingUseCase {
        return StopReceivingUseCase(createStreamingRepository())
    }

    fun createCloseConnectionUseCase(): CloseConnectionUseCase {
        return CloseConnectionUseCase(createStreamingRepository())
    }

    fun createStartSocketServerUseCase(): StartSocketServerUseCase {
        return StartSocketServerUseCase(createSignalingRepository())
    }
}