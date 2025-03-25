package com.example.p2pscreensharing.data.service

import android.util.Log
import com.example.p2pscreensharing.core.CaptureManager
import com.example.p2pscreensharing.core.SocketManager
import com.example.p2pscreensharing.data.model.FramePacket
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

    override fun startStreaming(ip: String?, port: Int?) {
        captureManager?.startCapturingFrames { frame ->
            streamingJob = CoroutineScope(Dispatchers.IO).launch {
                try {
//                    Log.d(
//                        "LogSocket",
//                        "send frame hex = ${frame.joinToString("") { "%02X".format(it) }}"
//                    )

                    socketManager.sendBytes(frame, ip = ip, port = port)
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
                    val encoded = socketManager.receiveBytes()

                    if (encoded == null) {
                        Log.d("LogSocket", "Waiting for more chunks...")
                        continue
                    }

                    val framePacket = FramePacket.decode(encoded)
                    if (framePacket == null) {
                        Log.w("LogSocket", "Failed to decode FramePacket")
                        continue
                    }

                    Log.d("LogSocket", "FramePacket received and decoded, size=${framePacket.payload.size}")

                    onFrameReceived(framePacket.payload)

                } catch (e: Exception) {
                    Log.e("LogSocket", "Exception in startReceiving(): ${e.message}", e)
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