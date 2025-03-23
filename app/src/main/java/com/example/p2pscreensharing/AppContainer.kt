package com.example.p2pscreensharing.di

import android.media.projection.MediaProjection
import com.example.p2pscreensharing.core.*
import com.example.p2pscreensharing.data.repository.*
import com.example.p2pscreensharing.data.service.*
import com.example.p2pscreensharing.domain.repository.*
import com.example.p2pscreensharing.domain.usecase.*

object AppContainer {

    // --- Singleton instance ---
    // TODO: sửa theo feedback
    val socketManager: SocketManager by lazy { BasicSocketManager() }

    // --- Runtime-created dependencies ---

    fun createCaptureManager(
        mediaProjection: MediaProjection,
        screenWidth: Int,
        screenHeight: Int,
        screenDensity: Int
    ): CaptureManager {
        return BasicCaptureManager(mediaProjection, screenWidth, screenHeight, screenDensity)
    }

    fun createStreamingService(
        captureManager: CaptureManager? = null
    ): StreamingService {
        return StreamingServiceReal(socketManager, captureManager)
    }

    fun createStreamingRepository(streamingService: StreamingService): StreamingRepository {
        return StreamingRepositoryImpl(streamingService)
    }

    fun createSignalingService(): SignalingService {
        return SignalingServiceReal(socketManager)
    }

    fun createSignalingRepository(): SignalingRepository {
        return SignalingRepositoryImpl(createSignalingService())
    }

    fun createStartStreamingUseCase(repo: StreamingRepository): StartStreamingUseCase {
        return StartStreamingUseCase(repo)
    }

    fun createStopStreamingUseCase(repo: StreamingRepository): StopStreamingUseCase {
        return StopStreamingUseCase(repo)
    }

    fun createStartReceivingUseCase(repo: StreamingRepository): StartReceivingUseCase {
        return StartReceivingUseCase(repo)
    }

    fun createStopReceivingUseCase(repo: StreamingRepository): StopReceivingUseCase {
        return StopReceivingUseCase(repo)
    }

    fun createCloseConnectionUseCase(repo: StreamingRepository): CloseConnectionUseCase {
        return CloseConnectionUseCase(repo)
    }

    fun createStartSocketServerUseCase(repo: SignalingRepository): StartSocketServerUseCase {
        return StartSocketServerUseCase(repo)
    }

    fun createConnectToPeerUseCase(repo: SignalingRepository): ConnectToPeerUseCase {
        return ConnectToPeerUseCase(repo)
    }
}
