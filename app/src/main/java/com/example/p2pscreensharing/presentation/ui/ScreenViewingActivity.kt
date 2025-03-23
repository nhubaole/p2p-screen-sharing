package com.example.p2pscreensharing.presentation.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pscreensharing.R
import com.example.p2pscreensharing.di.AppContainer
import com.example.p2pscreensharing.presentation.viewmodel.ScreenViewingViewModel

class ScreenViewingActivity : AppCompatActivity() {

    private lateinit var viewModel: ScreenViewingViewModel

    private lateinit var layoutWaiting: View
    private lateinit var layoutViewing: View
    private lateinit var imgSharedScreen: ImageView
    private lateinit var btnStopViewing: Button
    private lateinit var tvViewerId: TextView

    private var hasReceivedFirstFrame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_viewing)

        // --- Get port from Intent ---
        val port = intent.getIntExtra("listen_port", 0)
        if (port == 0) {
            finish()
            return
        }

        // --- UI binding ---
        layoutWaiting = findViewById(R.id.layoutWaiting)
        layoutViewing = findViewById(R.id.layoutViewing)
        imgSharedScreen = findViewById(R.id.imgSharedScreen)
        btnStopViewing = findViewById(R.id.btnStopViewing)
        tvViewerId = findViewById(R.id.tvViewerId)

        layoutWaiting.visibility = View.VISIBLE
        layoutViewing.visibility = View.GONE
        imgSharedScreen.visibility = View.GONE

        tvViewerId.text = port.toString()

        // --- Manual DI ---
        val streamingService = AppContainer.createStreamingService() // no CaptureManager
        val streamingRepo = AppContainer.createStreamingRepository(streamingService)
        val signalingRepo = AppContainer.createSignalingRepository()

        val startReceiving = AppContainer.createStartReceivingUseCase(streamingRepo)
        val stopReceiving = AppContainer.createStopReceivingUseCase(streamingRepo)
        val closeConnection = AppContainer.createCloseConnectionUseCase(streamingRepo)
        val startSocketServer = AppContainer.createStartSocketServerUseCase(signalingRepo)

        viewModel = ScreenViewingViewModel(
            startReceiving = startReceiving,
            stopReceiving = stopReceiving,
            closeConnection = closeConnection,
            startSocketServer = startSocketServer
        )

        // --- Observe frame and show ---
        viewModel.frameResult.observe(this) { frameBytes ->
            if (!hasReceivedFirstFrame) {
                hasReceivedFirstFrame = true
                layoutWaiting.visibility = View.GONE
                layoutViewing.visibility = View.VISIBLE
                imgSharedScreen.visibility = View.VISIBLE
            }

            val bitmap = BitmapFactory.decodeByteArray(frameBytes, 0, frameBytes.size)
            imgSharedScreen.setImageBitmap(bitmap)
        }

        // --- Start listening for screen sharing ---
        viewModel.startViewing(port)

        // --- Stop button ---
        btnStopViewing.setOnClickListener {
            viewModel.stopViewing()
            finish()
        }
    }

    override fun onDestroy() {
        viewModel.stopViewing()
        super.onDestroy()
    }
}
