package com.example.core.core

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer

class BasicTcpSocketManager : TcpSocketManager {

    private var socket: Socket? = null

    private var serverSocket: ServerSocket? = null

    private var outputStream: OutputStream? = null

    private var inputStream: InputStream? = null

    override suspend fun startServer(
        port: Int,
        onReady: (ip: String?, port: Int?) -> Unit,
        onClientConnected: () -> Unit
    ): Unit =
        withContext(
            Dispatchers.IO
        ) {
            serverSocket?.close()

            serverSocket = ServerSocket(port)

            onReady(
                getLocalIpAddress().orEmpty(),
                port
            )

            try {
                socket = serverSocket?.accept()
                Log.d("LogSocket", "Client connected!")

                withContext(Dispatchers.Main) {
                    onClientConnected()
                }

                setupStreams()
            } catch (e: SocketException) {
                Log.w("LogSocket", e.toString())
            }
        }

    override suspend fun connectToHost(ip: String, port: Int) = withContext(Dispatchers.IO) {
        socket = Socket(ip, port)

        Log.d("LogSocket", "Connecting on port $port ip ${ip}")

        setupStreams()
    }

    override suspend fun sendBytes(data: ByteArray): Unit = withContext(Dispatchers.IO) {
        try {
            val length = data.size
            val lengthBytes = ByteBuffer.allocate(4).putInt(length).array()

            outputStream?.write(lengthBytes)
            outputStream?.write(data)

            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun receiveBytes(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val lengthBytes = ByteArray(4)
            val lengthRead = inputStream?.readFully(lengthBytes) ?: return@withContext null
            val length = ByteBuffer.wrap(lengthBytes).int

            val dataBytes = ByteArray(length)
            val dataRead = inputStream?.readFully(dataBytes) ?: return@withContext null

            return@withContext dataBytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun closeConnection() {
        try {
            serverSocket?.close()
            socket?.close()
            inputStream?.close()
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupStreams() {
        outputStream = socket?.getOutputStream()
        inputStream = socket?.getInputStream()
    }

    private fun InputStream.readFully(buffer: ByteArray): Int {
        var bytesRead = 0
        while (bytesRead < buffer.size) {
            val result = this.read(buffer, bytesRead, buffer.size - bytesRead)
            if (result == -1) break
            bytesRead += result
        }
        return bytesRead
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