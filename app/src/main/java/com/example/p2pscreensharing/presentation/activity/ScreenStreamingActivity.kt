package com.example.p2pscreensharing.presentation.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.p2pscreensharing.R
import com.example.p2pscreensharing.presentation.fragment.ScreenSharingFragment
import com.example.p2pscreensharing.presentation.viewmodel.ConnectionSession

class ScreenStreamingActivity : AppCompatActivity() {

    private lateinit var tabShare: TextView
    private lateinit var tabView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acvitiy_screen_streaming)

        transparentStatusBar()

        tabShare = findViewById(R.id.tabShare)
        tabView = findViewById(R.id.tabView)

        tabShare.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    ScreenSharingFragment.newInstance(ConnectionSession.peerIp, ConnectionSession.peerPort)
                )
                .commit()
            updateTabUI(selected = tabShare, other = tabView)
        }

        tabView.setOnClickListener {
            switchFragment(ScreenViewingFragment())
            updateTabUI(selected = tabView, other = tabShare)
        }

        if (savedInstanceState == null) {
            tabShare.performClick()
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateTabUI(selected: TextView, other: TextView) {
        selected.setBackgroundResource(R.drawable.bg_tab_selected)
        selected.setTextColor(Color.WHITE)

        other.setBackgroundColor(Color.TRANSPARENT)
        other.setTextColor(Color.WHITE)
    }

    private fun transparentStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}