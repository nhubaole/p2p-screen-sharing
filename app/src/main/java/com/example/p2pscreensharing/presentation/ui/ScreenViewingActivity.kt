package com.example.p2pscreensharing.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appdependencies.CoreComponents.getVideoRecorder
import com.example.core.core.VideoRecorder
import com.example.p2pscreensharing.R
import com.example.p2pscreensharing.di.AppContainer
import com.example.p2pscreensharing.presentation.viewmodel.ScreenViewingViewModel
import java.io.File

class ScreenViewingActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_WRITE_EXTERNAL = 1001
    }

    private lateinit var viewModel: ScreenViewingViewModel
    private lateinit var videoRecorder: VideoRecorder

    private lateinit var layoutWaiting: View
    private lateinit var layoutViewing: View
    private lateinit var imgSharedScreen: ImageView
    private lateinit var btnToggleRecording: Button
    private lateinit var btnStopViewing: Button
    private lateinit var btnStopViewingWaiting: Button
    private lateinit var tvViewerId: TextView

    private var isRecording = false
    private var videoRecorderStarted = false
    private var hasReceivedFirstFrame = false
    private var port = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_viewing)

        viewModel = AppContainer.createScreenViewingViewModel()
        videoRecorder = getVideoRecorder()

        getConfigs()
        initViews()
        initObservers()
        startScreenViewing()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnToggleRecording.performClick()
            } else {
                Toast.makeText(this, "Permission denied. Cannot save video.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        viewModel.stopViewing()
        super.onDestroy()
    }

    private fun getConfigs() {
        port = intent.getIntExtra("listen_port", 0)
        if (port == 0) {
            finish()
            return
        }
    }

    private fun initViews() {
        layoutWaiting = findViewById(R.id.layoutWaiting)
        layoutViewing = findViewById(R.id.layoutViewing)
        imgSharedScreen = findViewById(R.id.imgSharedScreen)
        btnToggleRecording = findViewById(R.id.btnToggleRecording)
        btnStopViewing = findViewById(R.id.btnStopViewing)
        btnStopViewingWaiting = findViewById(R.id.btnStopViewing_Waiting)
        tvViewerId = findViewById(R.id.tvViewerId)

        layoutWaiting.visibility = View.VISIBLE
        layoutViewing.visibility = View.GONE
        imgSharedScreen.visibility = View.GONE

        btnToggleRecording.setOnClickListener {
            isRecording = !isRecording

            if (isRecording) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_EXTERNAL
                    )
                    isRecording = false
                    return@setOnClickListener
                }

                btnToggleRecording.text = "Stop Recording"
            } else {
                videoRecorder.stop()
                videoRecorderStarted = false
                btnToggleRecording.text = "Start Recording"
            }
        }

        btnStopViewing.setOnClickListener {
            viewModel.stopViewing()
            finish()
        }

        btnStopViewingWaiting.setOnClickListener {
            viewModel.stopViewing()
            finish()
        }
    }

    private fun initObservers() {
        viewModel.apply {
            frameResult.observe(this@ScreenViewingActivity) { frameBytes ->
                if (!hasReceivedFirstFrame) {
                    hasReceivedFirstFrame = true
                    layoutWaiting.visibility = View.GONE
                    layoutViewing.visibility = View.VISIBLE
                    imgSharedScreen.visibility = View.VISIBLE
                }

                val bitmap = BitmapFactory.decodeByteArray(frameBytes, 0, frameBytes.size)
                imgSharedScreen.setImageBitmap(bitmap)

                if (isRecording) {
                    if (!videoRecorderStarted) {
                        videoRecorderStarted = true
                        val outputFile = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            "shared_screen_record_${System.currentTimeMillis()}.mp4"
                        )

                        Log.d("VideoRecorder", "Start recording: ${bitmap.width}x${bitmap.height}")

                        videoRecorder.start(
                            width = 640,
                            height = 1280,
                            outputFile = outputFile
                        )
                    }

                    videoRecorder.encodeFrame(bitmap)
                }
            }
            peerEntity.observe(this@ScreenViewingActivity) { clientInfo ->
                tvViewerId.text = clientInfo?.ip
            }
        }
    }

    private fun startScreenViewing() {
        viewModel.startViewing(port)
    }
}
