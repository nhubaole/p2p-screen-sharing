package com.example.p2pscreensharing.core

import android.util.Log
import com.example.p2pscreensharing.data.model.ClientInfo
import com.example.p2pscreensharing.data.model.FrameChunkPacket
import com.example.p2pscreensharing.data.model.PeerRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpSocketManager : SocketManager {

    private var socket: DatagramSocket? = null

    private val sendSocket = DatagramSocket().apply { broadcast = true }

    private val chunkBuffer = mutableMapOf<Long, MutableMap<Int, ByteArray>>()

    override suspend fun startServer(
        port: Int,
        onReady: (ClientInfo?) -> Unit
    ) = withContext(Dispatchers.IO) {
        socket = DatagramSocket(port)
        Log.d("LogSocket", "Listening on port $port IP ${getLocalIpAddress()}")

        onReady(
            ClientInfo(
                id = null,
                ip = getLocalIpAddress().orEmpty(),
                port = port,
                role = PeerRole.RECEIVER
            )
        )
    }

    override suspend fun sendBytes(data: ByteArray, ip: String?, port: Int?): Unit =
        withContext(Dispatchers.IO) {
            if (ip == null || port == null) return@withContext

            try {
                val chunkSize = 8192
                val totalChunks = (data.size + chunkSize - 1) / chunkSize
                val frameId = System.currentTimeMillis()

                for (i in 0 until totalChunks) {
                    val start = i * chunkSize
                    val end = minOf(start + chunkSize, data.size)
                    val chunkData = data.copyOfRange(start, end)

                    val chunkPacket = FrameChunkPacket(
                        frameId = frameId,
                        chunkIndex = i,
                        totalChunks = totalChunks,
                        payload = chunkData
                    )

                    val encoded = FrameChunkPacket.encode(chunkPacket)
                    val sendPacket =
                        DatagramPacket(encoded, encoded.size, InetAddress.getByName(ip), port)

                    sendSocket.send(sendPacket)

                    Log.d("LogSocket", "Sent chunk $i/${totalChunks - 1}, size=${encoded.size}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override suspend fun receiveBytes(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val buffer = ByteArray(65507)
            val packet = DatagramPacket(buffer, buffer.size)
            socket?.receive(packet)

            val chunkPacket = FrameChunkPacket.decode(packet.data.copyOf(packet.length))
            if (chunkPacket == null) {
                Log.w("LogSocket", "Failed to decode chunk packet")
                return@withContext null
            }

            val frameId = chunkPacket.frameId
            val chunkIndex = chunkPacket.chunkIndex
            val totalChunks = chunkPacket.totalChunks

            Log.d(
                "LogSocket",
                "Chunk received — frameId=$frameId, index=$chunkIndex/$totalChunks, size=${chunkPacket.payload.size}"
            )

            val frameChunks = chunkBuffer.getOrPut(frameId) { mutableMapOf() }
            frameChunks[chunkIndex] = chunkPacket.payload

            Log.d("LogSocket", "Current collected: ${frameChunks.size}/$totalChunks")

            if (frameChunks.size == totalChunks) {
                val fullData = ByteArray(frameChunks.values.sumOf { it.size })
                var offset = 0
                for (i in 0 until totalChunks) {
                    val part = frameChunks[i]
                    if (part == null) {
                        Log.w("LogSocket", "Missing chunk at index $i, aborting frame.")
                        return@withContext null
                    }
                    System.arraycopy(part, 0, fullData, offset, part.size)
                    offset += part.size
                }

                chunkBuffer.remove(frameId)

                Log.d("LogSocket", "Frame reconstructed successfully, size=${fullData.size}")
                return@withContext fullData
            }

            return@withContext null
        } catch (e: Exception) {
            Log.e("LogSocket", "Exception in receiveBytes(): ${e.message}", e)
            return@withContext null
        }
    }

    override fun closeConnection() {
        socket?.close()
    }

    private fun getLocalIpAddress(): String? {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            interfaces.toList().flatMap { it.inetAddresses.toList() }
                .find { !it.isLoopbackAddress && it.hostAddress?.contains(".") == true }
                ?.hostAddress
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
