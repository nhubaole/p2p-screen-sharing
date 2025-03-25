package com.example.p2pscreensharing.core

import android.util.Log
import com.example.p2pscreensharing.data.model.ClientInfo
import com.example.p2pscreensharing.data.model.PeerRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpSocketManager : SocketManager {

    private var socket: DatagramSocket? = null
    private var remoteAddress: InetAddress? = null
    private var remotePort: Int? = null

    private val sendSocket = DatagramSocket().apply {
        broadcast = true
    }

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

    // No need to implement this for UDP
    override suspend fun connectToHost(ip: String, port: Int) = Unit

    override suspend fun sendBytes(data: ByteArray, ip: String?, port: Int?): Unit = withContext(Dispatchers.IO) {
        try {
//            val packet = DatagramPacket(data, data.size, InetAddress.getByName(ip), 8080)
//            Log.d("LogSocket", "Sending ${data.size} bytes")
//
//            socket?.send(packet)

            val sendPacket = DatagramPacket(data, data.size, InetAddress.getByName(ip), port ?: 0)

            Log.d("LogSocket", "Sending ${data.size} bytes")

            sendSocket.send(sendPacket)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun receiveBytes(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val buffer = ByteArray(65507)
            val packet = DatagramPacket(buffer, buffer.size)
            Log.d("LogSocket", "Chuẩn bị block")

            socket?.receive(packet)
            Log.d("LogSocket", "BLOCKED")

            remoteAddress = packet.address
            remotePort = packet.port

            return@withContext packet.data.copyOf(packet.length)
        } catch (e: Exception) {
            e.printStackTrace()
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
