package com.example.p2pscreensharing.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScreenSharingScreen(
    isSharing: Boolean,
    onStartSharing: () -> Unit,
    onStopSharing: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSharing) "You are currently sharing\nyour screen" else "Ready to share your screen",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isSharing)
                "Your screen is now being streamed live."
            else
                "Start a live stream of your device screen.\nThe other device will be able to view it in real-time.",
            textAlign = TextAlign.Center,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = if (isSharing) onStopSharing else onStartSharing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = if (isSharing) {
                ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF236DDF),
                    contentColor = Color.White
                )
            },
            border = if (isSharing) BorderStroke(1.dp, Color(0xFF3B82F6)) else null,
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = if (isSharing) "STOP SHARING" else "SHARE SCREEN",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
