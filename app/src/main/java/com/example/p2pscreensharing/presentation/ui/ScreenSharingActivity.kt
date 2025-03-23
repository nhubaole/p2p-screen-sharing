package com.example.p2pscreensharing.presentation.ui

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pscreensharing.R
import com.example.p2pscreensharing.di.AppContainer
import com.example.p2pscreensharing.presentation.viewmodel.ScreenSharingViewModel

class ScreenSharingActivity : AppCompatActivity() {

    private lateinit var viewModel: ScreenSharingViewModel
    private var mediaProjection: MediaProjection? = null

    private val mediaProjectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK || result.data == null) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                finish()
                return@registerForActivityResult
            }

            val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = projectionManager.getMediaProjection(result.resultCode, result.data!!)

            if (mediaProjection == null) {
                Toast.makeText(this, "MediaProjection is null", Toast.LENGTH_SHORT).show()
                finish()
                return@registerForActivityResult
            }

            setupScreenSharing()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_sharing)

        val viewerIp = intent.getStringExtra("viewer_ip") ?: ""
        val viewerPort = intent.getIntExtra("viewer_port", 0)

        if (viewerIp.isBlank() || viewerPort == 0) {
            Toast.makeText(this, "Invalid Viewer Info", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Step 1: Request media projection permission
        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(projectionManager.createScreenCaptureIntent())

        findViewById<Button>(R.id.btnStopSharing).setOnClickListener {
            viewModel.stopSharing()
            finish()
        }
    }

    private fun setupScreenSharing() {
        val mediaProjection = this.mediaProjection ?: return

        // Step 2: Get screen metrics
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        val screenDensity = metrics.densityDpi

        // Step 3: Manual DI
        val captureManager = AppContainer.createCaptureManager(
            mediaProjection, screenWidth, screenHeight, screenDensity
        )
        val streamingService = AppContainer.createStreamingService(captureManager)
        val streamingRepo = AppContainer.createStreamingRepository(streamingService)
        val signalingRepo = AppContainer.createSignalingRepository()

        val startStreaming = AppContainer.createStartStreamingUseCase(streamingRepo)
        val stopStreaming = AppContainer.createStopStreamingUseCase(streamingRepo)
        val closeConnection = AppContainer.createCloseConnectionUseCase(streamingRepo)
        val connectToPeer = AppContainer.createConnectToPeerUseCase(signalingRepo)

        viewModel = ScreenSharingViewModel(
            startStreaming = startStreaming,
            stopStreaming = stopStreaming,
            closeConnection = closeConnection,
            connectToPeer = connectToPeer
        )

        // Step 4: Start sharing
        val viewerIp = intent.getStringExtra("viewer_ip") ?: ""
        val viewerPort = intent.getIntExtra("viewer_port", 0)
        viewModel.connectAndStartSharing(viewerIp, viewerPort)
    }

    override fun onDestroy() {
        viewModel.stopSharing()
        mediaProjection?.stop()
        super.onDestroy()
    }
}
