package com.example.p2pscreensharing.presentation.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pscreensharing.R
import com.example.p2pscreensharing.di.AppContainer
import com.example.p2pscreensharing.presentation.service.StreamingForegroundService
import com.example.p2pscreensharing.presentation.viewmodel.ScreenSharingViewModel

class ScreenSharingActivity : AppCompatActivity() {

    private lateinit var viewModel: ScreenSharingViewModel
    private var mediaProjection: MediaProjection? = null

    private var viewerIp = ""
    private var viewerPort = 0

    @RequiresApi(Build.VERSION_CODES.O)
    private val mediaProjectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()

                finish()
                return@registerForActivityResult
            }

            val projectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

            mediaProjection = projectionManager.getMediaProjection(result.resultCode, result.data!!)

            mediaProjection?.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    super.onStop()
                    Log.d("MediaProjection", "Stopped by system or user")
                }
            }, Handler(Looper.getMainLooper()))

            if (mediaProjection == null) {
                Toast.makeText(this, "MediaProjection is null", Toast.LENGTH_SHORT).show()

                finish()
                return@registerForActivityResult
            }

            injectDependencies()

            startSharing()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_sharing)

        //TODO: handle crash if server not started yet

        getConfigs()
        startStreamingForegroundService()
        requestMediaProjectionPermission()

        initViews()
    }

    override fun onDestroy() {
        try {
            viewModel.stopSharing()
            mediaProjection?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    private fun getConfigs() {
        viewerIp = intent.getStringExtra("viewer_ip") ?: ""
        viewerPort = intent.getIntExtra("viewer_port", 0)

        if (viewerIp.isBlank() || viewerPort == 0) {
            Toast.makeText(this, "Invalid Viewer Info", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    private fun injectDependencies() {
        val mediaProjection = this.mediaProjection ?: return

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        val screenDensity = metrics.densityDpi

        val captureManager = AppContainer.createCaptureManager(
            mediaProjection, screenWidth, screenHeight, screenDensity
        )

        val streamingService = AppContainer.createStreamingService(captureManager)
        val streamingRepo = AppContainer.createStreamingRepository(streamingService)

        val startStreaming = AppContainer.createStartStreamingUseCase(streamingRepo)
        val stopStreaming = AppContainer.createStopStreamingUseCase(streamingRepo)
        val closeConnection = AppContainer.createCloseConnectionUseCase(streamingRepo)

        viewModel = ScreenSharingViewModel(
            startStreaming = startStreaming,
            stopStreaming = stopStreaming,
            closeConnection = closeConnection,
        )
    }

    private fun initViews() {
        findViewById<Button>(R.id.btnStopSharing).setOnClickListener {
            viewModel.stopSharing()
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startStreamingForegroundService() {
        val serviceIntent = Intent(this, StreamingForegroundService::class.java)
        startForegroundService(serviceIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestMediaProjectionPermission() {
        val projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(projectionManager.createScreenCaptureIntent())
    }

    private fun startSharing() {
        try {
            viewModel.startSharing(viewerIp, viewerPort)
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }
}
