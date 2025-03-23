package com.example.p2pscreensharing.data.service

import com.example.p2pscreensharing.core.CaptureManager
import com.example.p2pscreensharing.core.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StreamingServiceReal(
    private val socketManager: SocketManager,
    private val captureManager: CaptureManager? = null,
) : StreamingService {

    private var streamingJob: Job? = null
    private var receivingJob: Job? = null

    override fun startStreaming() {
        captureManager?.startCapturingFrames { frame ->
            streamingJob = CoroutineScope(Dispatchers.IO).launch {
                try {
                    socketManager.send(frame)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun stopStreaming() {
        captureManager?.stopCapturing()
        streamingJob?.cancel()
        streamingJob = null
    }

    override fun startReceiving(onFrameReceived: (ByteArray) -> Unit) {
        receivingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val frame = socketManager.receive()
                    frame?.let { onFrameReceived(it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun stopReceiving() {
        receivingJob?.cancel()
        receivingJob = null
    }

    override fun closeConnection() {
        socketManager.closeConnection()
    }
}