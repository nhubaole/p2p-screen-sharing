package com.example.p2pscreensharing.di

import android.media.projection.MediaProjection
import com.example.p2pscreensharing.core.*
import com.example.p2pscreensharing.data.repository.*
import com.example.p2pscreensharing.data.service.*
import com.example.p2pscreensharing.domain.repository.*
import com.example.p2pscreensharing.domain.usecase.*

object AppContainer {

    private var socketManagerInstance: SocketManager? = null
    fun getSocketManager(): SocketManager {
        if (socketManagerInstance == null) {
            socketManagerInstance = UdpSocketManager()
        }
        return socketManagerInstance!!
    }

    private var signalingServiceInstance: SignalingService? = null
    fun getSignalingService(): SignalingService {
        if (signalingServiceInstance == null) {
            signalingServiceInstance = SignalingServiceReal(getSocketManager())
        }
        return signalingServiceInstance!!
    }

    private var signalingRepoInstance: SignalingRepository? = null
    fun getSignalingRepository(): SignalingRepository {
        if (signalingRepoInstance == null) {
            signalingRepoInstance = SignalingRepositoryImpl(getSignalingService())
        }
        return signalingRepoInstance!!
    }

    private var captureManagerInstance: CaptureManager? = null
    fun getCaptureManager(): CaptureManager {
        if (captureManagerInstance == null) {
            captureManagerInstance = BasicCaptureManager()
        }
        return captureManagerInstance!!
    }

    fun createStreamingService(captureManager: CaptureManager?): StreamingService {
        return StreamingServiceReal(getSocketManager(), captureManager)
    }

    fun createStreamingRepository(service: StreamingService): StreamingRepository {
        return StreamingRepositoryImpl(service)
    }

    fun createStartStreamingUseCase(repo: StreamingRepository) = StartStreamingUseCase(repo)

    fun createStopStreamingUseCase(repo: StreamingRepository) = StopStreamingUseCase(repo)

    fun createStartReceivingUseCase(repo: StreamingRepository) = StartReceivingUseCase(repo)

    fun createStopReceivingUseCase(repo: StreamingRepository) = StopReceivingUseCase(repo)

    fun createCloseConnectionUseCase(repo: StreamingRepository) = CloseConnectionUseCase(repo)

    fun createStartSocketServerUseCase(repo: SignalingRepository) = StartSocketServerUseCase(repo)
}