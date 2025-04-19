package com.example.p2pscreensharing.presentation.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.p2pscreensharing.R
import com.example.p2pscreensharing.di.AppContainer
import com.example.p2pscreensharing.presentation.ui.ConnectToPeerScreen
import com.example.p2pscreensharing.presentation.viewmodel.MainViewModel
import com.example.p2pscreensharing.service.ConnectionForegroundService

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var service: ConnectionForegroundService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as? ConnectionForegroundService.LocalBinder
            service = localBinder?.getService()
            isBound = true

            val info = service?.getConnectionInfo()
            info?.takeIf { it.isConnected }?.let {
                Log.d(
                    "MainActivity",
                    "IsConnected=${it.isConnected}, IP=${it.peerIp}, Time=${it.connectedTime}"
                )
                viewModel.updateConnectionTime(it.connectedTime.toString())
                viewModel.updateIpInfo(it.peerIp)
                viewModel.updateConnectionStatus(true)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = AppContainer.createMainViewModel()

        initViews()
        initObservers()
        initData()
        transparentStatusBar()
    }

    override fun onStart() {
        super.onStart()
//        val intent = Intent(this, ConnectionForegroundService::class.java)
//        ContextCompat.startForegroundService(this, intent)
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    private fun initViews() {
        setContent {
            ConnectToPeerScreen(
                viewModel = viewModel,
                onSettingClick = {

                },
                onConnectClick = {
                    viewModel.connectToPeer(peerIp = it)
                },

                onDisconnectClick = {
                    if (isBound) {
                        unbindService(serviceConnection)
                        isBound = false
                    }
                    this.stopService(Intent(this, ConnectionForegroundService::class.java))
                    viewModel.disconnect()
                }
            )
        }
    }

    private fun initObservers() {
        viewModel.apply {
            peerConnectedResult.observe(this@MainActivity) {
                val intent = Intent(this@MainActivity, ConnectionForegroundService::class.java).apply {
                    putExtra("peerIp", it)
                }
                ContextCompat.startForegroundService(this@MainActivity, intent)
            }
        }
    }

    private fun transparentStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun initData() {
        val info = service?.getConnectionInfo()

        if (info?.isConnected == true) {
            Log.d(
                "MainActivity",
                "IsConnected=${info.isConnected}, IP=${info.peerIp}, Time=${info.connectedTime}"
            )
            viewModel.updateConnectionTime(info.connectedTime.toString())
            viewModel.updateIpInfo(info.peerIp)
            viewModel.updateConnectionStatus(true)
        } else {
            viewModel.initData()
            viewModel.updateIpInfo(getLocalIpAddress() ?: "Unknown")
        }
    }


    private fun getLocalIpAddress(): String? {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            interfaces.toList().flatMap { it.inetAddresses.toList() }
                .find { !it.isLoopbackAddress && it.hostAddress?.contains(".") == true }
                ?.hostAddress
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}