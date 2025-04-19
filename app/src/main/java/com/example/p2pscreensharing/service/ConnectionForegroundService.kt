package com.example.p2pscreensharing.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.p2pscreensharing.R

class ConnectionForegroundService : Service() {

    private val binder = LocalBinder()

    private var isConnected = false
    private var connectedStartTime: Long = 0L
    private var peerIp: String = ""

    companion object {
        const val CHANNEL_ID = "connection_status_channel"
        const val NOTIFICATION_ID = 1
    }

    inner class LocalBinder : Binder() {
        fun getService(): ConnectionForegroundService = this@ConnectionForegroundService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isConnected) {
            intent?.let {
                peerIp = it.getStringExtra("peerIp") ?: ""
                isConnected = true
                connectedStartTime = System.currentTimeMillis()
            }

            showNotification()
        }

        return START_STICKY
    }


    private fun showNotification() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Connected to $peerIp")
            .setContentText("Tap to open app")
            .setSmallIcon(R.drawable.ic_miru)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    // Public API
    fun getConnectionInfo(): ConnectionInfo {
        return ConnectionInfo(
            isConnected = isConnected,
            peerIp = peerIp,
            connectedTime = System.currentTimeMillis() - connectedStartTime
        )
    }

    fun stopConnection() {
        isConnected = false
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Connection Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows connection status of P2P sharing"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    data class ConnectionInfo(
        val isConnected: Boolean,
        val peerIp: String,
        val connectedTime: Long
    )
}