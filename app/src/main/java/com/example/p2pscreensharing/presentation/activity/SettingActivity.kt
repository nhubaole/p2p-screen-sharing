package com.example.p2pscreensharing.presentation.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pscreensharing.presentation.ui.SettingScreen

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingScreen(onBackClick = { finish() }, onOptionClick = {
                if (it == "Screen") {
                    startActivity(Intent(this, ScreenStreamingActivity::class.java))
                }
            })
        }

        transparentStatusBar()
    }

    private fun transparentStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}