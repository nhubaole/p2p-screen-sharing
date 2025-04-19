package com.example.p2pscreensharing.presentation.ui

import AppTypography
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.p2pscreensharing.R

@Composable
fun ConnectToPeerScreen(
    ipAddress: String = "127.123.21.12",
    connectionStatus: String = "Not Connected",
    isConnected: Boolean = false,
    time: String = "00:00:00",
    onConnectClick: () -> Unit
) {
    val statusColor = if (isConnected) Color(0xFF14AE5C) else Color.Red

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1D2031))
                        .padding(8.dp)
                )
            }

            Text(
                text = "Welcome to",
                style = AppTypography.bodyLarge.copy(
                    color = Color(0xFF7C7F90)
                )
            )

            Image(
                painter = painterResource(id = R.drawable.ic_miru),
                contentDescription = "Miru",
                modifier = Modifier
                    .size(48.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .clip(RoundedCornerShape(200.dp))
                    .border(1.dp, Color(0xFF236DDF), RoundedCornerShape(200.dp))
                    .background(Color(0xFF1D2031))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_pin),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Your IP",
                        color = Color.White,
                        style = AppTypography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = ipAddress,
                        color = Color(0xFF7C7F90),
                        style = AppTypography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .border(6.dp, Color(0xFF236DDF), CircleShape)
                    .clickable { onConnectClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_turn_on),
                        tint = Color.White,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "CONNECT",
                        color = Color.White,
                        style = AppTypography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    buildAnnotatedString {
                        append("Status : ")
                        withStyle(SpanStyle(color = statusColor)) {
                            append(connectionStatus)
                        }
                    },
                    style = AppTypography.labelLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = time,
                    color = Color.White,
                    style = AppTypography.displaySmall
                )
            }
        }
    }
}
