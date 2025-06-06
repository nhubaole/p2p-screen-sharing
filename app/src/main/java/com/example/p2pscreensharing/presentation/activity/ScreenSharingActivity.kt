package com.example.p2pscreensharing.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pscreensharing.R
import com.example.p2pscreensharing.presentation.ui.ScreenSharingScreen
import com.example.p2pscreensharing.service.StreamingForegroundService

class ScreenSharingActivity : AppCompatActivity() {

    private var viewerIp = ""
    private var viewerPort = 0

    @RequiresApi(Build.VERSION_CODES.O)
    private val mediaProjectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK || result.data == null) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                finish()
                return@registerForActivityResult
            }

            val metrics = DisplayMetrics().also {
                windowManager.defaultDisplay.getRealMetrics(it)
            }

            val serviceIntent = Intent(this, StreamingForegroundService::class.java).apply {
                action = StreamingForegroundService.ACTION_START
                putExtra("result_code", result.resultCode)
                putExtra("result_data", result.data)
                putExtra("width", metrics.widthPixels)
                putExtra("height", metrics.heightPixels)
                putExtra("density", metrics.densityDpi)
                putExtra("viewer_ip", viewerIp)
                putExtra("viewer_port", viewerPort)
            }

            startForegroundService(serviceIntent)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_sharing)

        viewerIp = intent.getStringExtra("viewer_ip") ?: ""
        viewerPort = intent.getIntExtra("viewer_port", 0)

//        if (viewerIp.isBlank() || viewerPort == 0) {
//            Toast.makeText(this, "Invalid Viewer Info", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }

        findViewById<Button>(R.id.btnStopSharing).setOnClickListener {
            stopSharing()
        }

        requestMediaProjectionPermission()
    }

    private fun stopSharing() {
        val stopIntent = Intent(this, StreamingForegroundService::class.java).apply {
            action = StreamingForegroundService.ACTION_STOP
        }
        startService(stopIntent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestMediaProjectionPermission() {
        val projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(projectionManager.createScreenCaptureIntent())
    }
}
