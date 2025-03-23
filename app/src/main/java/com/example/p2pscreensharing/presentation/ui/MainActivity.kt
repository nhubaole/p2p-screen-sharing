package com.example.p2pscreensharing.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pscreensharing.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnShare = findViewById<Button>(R.id.btnShareScreen)
        val btnView = findViewById<Button>(R.id.btnViewScreen)

        btnShare.setOnClickListener {
            val viewerIp = "192.168.66.220"
            val viewerPort = 8080

            val intent = Intent(this, ScreenSharingActivity::class.java).apply {
                putExtra("viewer_ip", viewerIp)
                putExtra("viewer_port", viewerPort)
            }
            startActivity(intent)
        }

        btnView.setOnClickListener {
            val listenPort = 8080

            val intent = Intent(this, ScreenViewingActivity::class.java).apply {
                putExtra("listen_port", listenPort)
            }
            startActivity(intent)
        }
    }
}