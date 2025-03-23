package com.example.p2pscreensharing.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class BasicSocketManager : SocketManager {
    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    override suspend fun startServer(port: Int) = withContext(Dispatchers.IO) {
        serverSocket = ServerSocket(port)
        socket = serverSocket?.accept()

        setupStreams()
    }

    override suspend fun connectToHost(ip: String, port: Int) = withContext(Dispatchers.IO) {
        socket = Socket(ip, port)

        setupStreams()
    }

    override suspend fun send(data: ByteArray): Unit = withContext(Dispatchers.IO) {
        try {
            outputStream?.write(data)
            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun receive(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val buffer = ByteArray(1024 * 64)
            val bytesRead = inputStream?.read(buffer) ?: -1
            if (bytesRead > 0) {
                buffer.copyOf(bytesRead)
            } else {
                null
            }
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
}