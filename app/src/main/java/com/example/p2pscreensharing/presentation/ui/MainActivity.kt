package com.example.p2pscreensharing.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pscreensharing.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private val PORT = 5555

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        val btnShare = findViewById<Button>(R.id.btnShareScreen)
        val btnView = findViewById<Button>(R.id.btnViewScreen)

        btnShare.setOnClickListener {
            showEnterViewerDialog()
        }

        btnView.setOnClickListener {
            val intent = Intent(this, ScreenViewingActivity::class.java).apply {
                putExtra("listen_port", PORT)
            }

            startActivity(intent)
        }
    }

    private fun showEnterViewerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_enter_id, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogView)

        val edtViewerId = dialogView.findViewById<EditText>(R.id.edtViewerId)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmViewer)

        btnConfirm.setOnClickListener {
            val viewerIp = edtViewerId.text.toString().trim()

            if (viewerIp.isEmpty()) {
                Toast.makeText(this, "Please enter a valid ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            bottomSheetDialog.dismiss()

            val intent = Intent(this, ScreenSharingActivity::class.java).apply {
                putExtra("viewer_ip", viewerIp)
                putExtra("viewer_port", PORT)
            }
            startActivity(intent)
        }

        bottomSheetDialog.show()
    }
}