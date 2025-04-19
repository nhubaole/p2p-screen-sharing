package com.example.p2pscreensharing.presentation.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.appdependencies.CoreComponents.getVideoRecorder
import com.example.core.core.VideoRecorder
import com.example.p2pscreensharing.R
import com.example.p2pscreensharing.di.AppContainer
import com.example.p2pscreensharing.presentation.viewmodel.ScreenViewingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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

    private var latestBitmap: Bitmap? = null
    private var recordingJob: Job? = null

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
                recordingJob?.cancel()
                recordingJob = null
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

                latestBitmap = bitmap

                if (!videoRecorderStarted) {
                    videoRecorderStarted = true

                    val (safeWidth, safeHeight) = scaleDownToMaxSize(bitmap.width, bitmap.height, 720, 1280)
                    val outputFile = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "shared_screen_record_${System.currentTimeMillis()}.mp4"
                    )

                    videoRecorder.start(safeWidth, safeHeight, outputFile)

                    recordingJob = lifecycleScope.launch(Dispatchers.Default) {
                        while (isActive) {
                            latestBitmap?.let {
                                videoRecorder.encodeFrame(it)
                            }
                            delay(33) // about 30 fps
                        }
                    }
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

    private fun scaleDownToMaxSize(originalWidth: Int, originalHeight: Int, maxWidth: Int, maxHeight: Int): Pair<Int, Int> {
        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()

        var newWidth = originalWidth
        var newHeight = originalHeight

        if (originalWidth > maxWidth) {
            newWidth = maxWidth
            newHeight = (maxWidth / aspectRatio).toInt()
        }

        if (newHeight > maxHeight) {
            newHeight = maxHeight
            newWidth = (maxHeight * aspectRatio).toInt()
        }

        newWidth -= newWidth % 16
        newHeight -= newHeight % 16

        return Pair(newWidth, newHeight)
    }
}
