package com.example.p2pscreensharing.core

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer

class BasicSocketManager : SocketManager {
    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    override suspend fun startServer(port: Int) = withContext(Dispatchers.IO) {
        serverSocket = ServerSocket(port)
        Log.d("LogSocket", "Listening on port $port ip ${getLocalIpAddress()}")

        socket = serverSocket?.accept()
        Log.d("LogSocket", "Client connected!")

        setupStreams()
    }

    override suspend fun connectToHost(ip: String, port: Int) = withContext(Dispatchers.IO) {
        Log.d("LogSocket", "Connecting on port $port ip ${ip}")
        socket = Socket(ip, port)

        setupStreams()
    }

    override suspend fun send(data: ByteArray): Unit = withContext(Dispatchers.IO) {
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

    override suspend fun receive(): ByteArray? = withContext(Dispatchers.IO) {
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
            socket?.close()
            serverSocket?.close()
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

    fun InputStream.readFully(buffer: ByteArray): Int {
        var bytesRead = 0
        while (bytesRead < buffer.size) {
            val result = this.read(buffer, bytesRead, buffer.size - bytesRead)
            if (result == -1) break
            bytesRead += result
        }
        return bytesRead
    }

    fun getLocalIpAddress(): String? {
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