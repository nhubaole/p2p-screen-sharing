package com.example.p2pscreensharing.presentation.ui

import AppTypography
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.p2pscreensharing.R

data class SettingItem(
    val label: String,
    val iconRes: Int
)


@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    onOptionClick: (String) -> Unit
) {
    val items = listOf(
        SettingItem("Screen", R.drawable.ic_mobile),
        SettingItem("Clipboard & Content", R.drawable.ic_notepad),
        SettingItem("File", R.drawable.ic_folder),
        SettingItem("Web & Session", R.drawable.ic_global_search),
        SettingItem("Control & Input", R.drawable.ic_press),
        SettingItem("Notification & Info", R.drawable.ic_notification),
        SettingItem("Automation", R.drawable.ic_reload_circle),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF10172A))
            .padding(horizontal = 16.dp, vertical = 40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1D2031))
                    .clickable { onBackClick() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "Setting",
                style = AppTypography.titleLarge.copy(color = Color.White)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1D2031))
                        .clickable { onOptionClick(item.label) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF236DDF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = item.label,
                        style = AppTypography.bodyLarge.copy(color = Color.White),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "Next",
                        tint = Color(0xFF434363),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
