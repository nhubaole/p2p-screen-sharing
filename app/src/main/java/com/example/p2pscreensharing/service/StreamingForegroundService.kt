package com.example.p2pscreensharing.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.appdependencies.CoreComponents.getCaptureManager
import com.example.appdependencies.DomainComponents.createStartStreamingUseCase
import com.example.appdependencies.DomainComponents.createStopStreamingUseCase
import com.example.core.core.BasicCaptureManager
import com.example.core.core.CaptureConfig
import com.example.p2pscreensharing.R

class StreamingForegroundService : Service() {

    companion object {
        const val ACTION_START = "com.example.p2pscreensharing.START"
        const val ACTION_STOP = "com.example.p2pscreensharing.STOP"
        private const val CHANNEL_ID = "screen_share"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> handleStop()
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleStart(intent: Intent) {
        startForeground(1, buildNotification())

        val resultCode = intent.getIntExtra("result_code", Activity.RESULT_CANCELED)
        val resultData = intent.getParcelableExtra<Intent>("result_data") ?: return

        val width = intent.getIntExtra("width", 0)
        val height = intent.getIntExtra("height", 0)
        val density = intent.getIntExtra("density", 0)
        val ip = intent.getStringExtra("viewer_ip") ?: return
        val port = intent.getIntExtra("viewer_port", 0)
        if (port == 0) return

        val config = CaptureConfig(applicationContext, width, height, density)

        getCaptureManager().let { captureManager ->
            if (captureManager is BasicCaptureManager) {
                captureManager.initProjection(resultCode, resultData, config)
            }

            createStartStreamingUseCase()(ip, port)
        }
    }

    private fun handleStop() {
        createStopStreamingUseCase()()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildNotification(): Notification {
        createNotificationChannelIfNeeded()

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Screen sharing in progress")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelIfNeeded() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Screen Sharing",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
