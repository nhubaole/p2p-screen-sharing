package com.example.p2pscreensharing.presentation.activity

import ScreenViewingScreen
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appdependencies.CoreComponents.getVideoRecorder
import com.example.core.core.VideoRecorder
import com.example.p2pscreensharing.di.AppContainer
import com.example.p2pscreensharing.presentation.viewmodel.ScreenViewingViewModel
import kotlinx.coroutines.*
import java.io.File

class ScreenViewingFragment : Fragment() {

    private lateinit var viewModel: ScreenViewingViewModel
    private lateinit var videoRecorder: VideoRecorder

    private var port: Int = 0
    private var recordingJob: Job? = null

    private var isRecording by mutableStateOf(false)
    private var videoRecorderStarted = false
    private var hasReceivedFirstFrame = false

    private var latestBitmap by mutableStateOf<Bitmap?>(null)
    private var isViewing by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        port = arguments?.getInt("listen_port") ?: 7123
        if (port == 0) {
            Toast.makeText(requireContext(), "Invalid port", Toast.LENGTH_SHORT).show()
            activity?.onBackPressedDispatcher?.onBackPressed()
            return
        }

        viewModel = AppContainer.createScreenViewingViewModel()
        videoRecorder = getVideoRecorder()

        initObservers()
        viewModel.startViewing(port)
    }

    override fun onDestroy() {
        viewModel.stopViewing()
        super.onDestroy()
    }

    private fun initObservers() {
        viewModel.frameResult.observe(this) { frameBytes ->
            if (!hasReceivedFirstFrame) {
                hasReceivedFirstFrame = true
                isViewing = true
            }

            val bitmap = BitmapFactory.decodeByteArray(frameBytes, 0, frameBytes.size)
            latestBitmap = bitmap

            if (isRecording && !videoRecorderStarted) {
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
                        delay(33) // ~30fps
                    }
                }
            }
        }
    }

    private fun stopRecording() {
        videoRecorder.stop()
        videoRecorderStarted = false
        recordingJob?.cancel()
        recordingJob = null
    }

    private fun toggleRecording() {
        isRecording = !isRecording

        if (isRecording) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1001
                )
                isRecording = false
                return
            }
        } else {
            stopRecording()
        }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ScreenViewingScreen(
                    isViewing = isViewing,
                    remoteScreenBitmap = latestBitmap,
//                    onToggleRecording = { toggleRecording() },
//                    isRecording = isRecording,
//                    onStopViewing = {
//                        viewModel.stopViewing()
//                        activity?.onBackPressedDispatcher?.onBackPressed()
//                    }
                )
            }
        }
    }

    companion object {
        fun newInstance(port: Int): ScreenViewingFragment {
            return ScreenViewingFragment().apply {
                arguments = Bundle().apply {
                    putInt("listen_port", port)
                }
            }
        }
    }
}