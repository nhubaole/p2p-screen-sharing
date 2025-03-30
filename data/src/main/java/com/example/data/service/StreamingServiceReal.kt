package com.example.data.service

import android.util.Log
import com.example.core.core.CaptureManager
import com.example.core.core.SocketManager
import com.example.data.model.FrameChunkPacket
import com.example.data.model.FramePacket
import com.example.data.model.encodeToChunks
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
                    val framePacket = FramePacket(payload = frame)

                    val chunkedPackets = framePacket.encodeToChunks()

                    chunkedPackets.forEachIndexed { index, chunk ->
                        socketManager.sendBytes(chunk, ip = ip, port = port)

                        Log.d(
                            "LogSocket",
                            "Sent chunk $index/${chunkedPackets.size - 1}, size=${chunk.size}"
                        )
                    }

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

    override fun startReceiving(onFrameReceived: (FramePacket) -> Unit) {
        val chunkBuffer = mutableMapOf<Long, MutableMap<Int, ByteArray>>()

        receivingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val rawData = socketManager.receiveBytes()
                    if (rawData == null) continue

                    val chunkPacket = FrameChunkPacket.decode(rawData) ?: continue
                    val frameId = chunkPacket.frameId
                    val chunkIndex = chunkPacket.chunkIndex
                    val totalChunks = chunkPacket.totalChunks

                    val frameChunks = chunkBuffer.getOrPut(frameId) { mutableMapOf() }
                    frameChunks[chunkIndex] = chunkPacket.payload

                    if (frameChunks.size == totalChunks) {
                        val fullData = ByteArray(frameChunks.values.sumOf { it.size })
                        var offset = 0
                        for (i in 0 until totalChunks) {
                            val part = frameChunks[i] ?: continue
                            System.arraycopy(part, 0, fullData, offset, part.size)
                            offset += part.size
                        }

                        chunkBuffer.remove(frameId)

                        val framePacket = FramePacket.decode(fullData)
                        onFrameReceived(framePacket)
                    }
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