package com.example.core.core

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpSocketManager : SocketManager {

    private var socket: DatagramSocket? = null

    private val sendSocket = DatagramSocket().apply { broadcast = true }

    override suspend fun startServer(
        port: Int,
        onReady: (ip: String?, port: Int?) -> Unit
    ) = withContext(Dispatchers.IO) {
        socket = DatagramSocket(port)
        Log.d("LogSocket", "Listening on port $port IP ${getLocalIpAddress()}")

        onReady(
            getLocalIpAddress().orEmpty(),
            port
        )
    }

    override suspend fun sendBytes(data: ByteArray, ip: String?, port: Int?): Unit =
        withContext(Dispatchers.IO) {
            if (ip == null || port == null) return@withContext
            try {
                val packet = DatagramPacket(data, data.size, InetAddress.getByName(ip), port)
                sendSocket.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override suspend fun receiveBytes(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val buffer = ByteArray(65507)
            val packet = DatagramPacket(buffer, buffer.size)
            socket?.receive(packet)
            return@withContext packet.data.copyOf(packet.length)
        } catch (e: Exception) {
            Log.e("LogSocket", "Exception in receiveBytesRaw(): ${e.message}", e)
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
