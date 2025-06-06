package com.example.p2pscreensharing.presentation.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.p2pscreensharing.presentation.ui.ScreenSharingScreen
import com.example.p2pscreensharing.service.StreamingForegroundService

class ScreenSharingFragment : Fragment() {

    private var viewerIp: String = ""
    private var viewerPort: Int = 0
    private val isSharingState = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    private val mediaProjectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK || result.data == null) {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val metrics = DisplayMetrics().also {
                requireActivity().windowManager.defaultDisplay.getRealMetrics(it)
            }

            val serviceIntent = Intent(requireContext(), StreamingForegroundService::class.java).apply {
                action = StreamingForegroundService.ACTION_START
                putExtra("result_code", result.resultCode)
                putExtra("result_data", result.data)
                putExtra("width", metrics.widthPixels)
                putExtra("height", metrics.heightPixels)
                putExtra("density", metrics.densityDpi)
                putExtra("viewer_ip", viewerIp)
                putExtra("viewer_port", viewerPort)
            }

            requireContext().startForegroundService(serviceIntent)
            isSharingState.value = true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewerIp = arguments?.getString("viewer_ip") ?: ""
        viewerPort = arguments?.getInt("viewer_port") ?: 0

//        if (viewerIp.isBlank() || viewerPort == 0) {
//            Toast.makeText(requireContext(), "Invalid Viewer Info", Toast.LENGTH_SHORT).show()
//            activity?.onBackPressedDispatcher?.onBackPressed()
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestMediaProjectionPermission() {
        val projectionManager =
            requireContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(projectionManager.createScreenCaptureIntent())
    }

    private fun stopSharing() {
        val stopIntent = Intent(requireContext(), StreamingForegroundService::class.java).apply {
            action = StreamingForegroundService.ACTION_STOP
        }
        requireContext().startService(stopIntent)
        isSharingState.value = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ScreenSharingScreen(
                    isSharing = isSharingState.value,
                    onStartSharing = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            requestMediaProjectionPermission()
                        } else {
                            Toast.makeText(requireContext(), "Unsupported version", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onStopSharing = { stopSharing() }
                )
            }
        }
    }

    companion object {
        fun newInstance(viewerIp: String, viewerPort: Int): ScreenSharingFragment {
            return ScreenSharingFragment().apply {
                arguments = Bundle().apply {
                    putString("viewer_ip", viewerIp)
                    putInt("viewer_port", viewerPort)
                }
            }
        }
    }
}
