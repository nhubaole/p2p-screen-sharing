package com.example.p2pscreensharing.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.p2pscreensharing.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnShare = findViewById<Button>(R.id.btnShareScreen)
        val btnView = findViewById<Button>(R.id.btnViewScreen)

        btnShare.setOnClickListener {
            // TODO: Replace with actual input (from dialog or screen)
            val viewerIp = "192.168.1.101"
            val viewerPort = 8080

            val intent = Intent(this, ScreenSharingActivity::class.java).apply {
                putExtra("viewer_ip", viewerIp)
                putExtra("viewer_port", viewerPort)
            }
            startActivity(intent)
        }

        btnView.setOnClickListener {
            // TODO: Replace with actual input (from dialog or screen)
            val listenPort = 8080

            val intent = Intent(this, ScreenViewingActivity::class.java).apply {
                putExtra("listen_port", listenPort)
            }
            startActivity(intent)
        }
    }
}