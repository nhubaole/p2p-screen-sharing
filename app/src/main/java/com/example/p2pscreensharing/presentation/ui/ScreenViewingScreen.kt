import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2pscreensharing.R

@Composable
fun ScreenViewingScreen(
    isViewing: Boolean,
    remoteScreenBitmap: Bitmap?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isViewing && remoteScreenBitmap != null) {
            val imageBitmap = remember(remoteScreenBitmap) {
                remoteScreenBitmap.asImageBitmap()
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "You are now viewing the other device's screen in real-time.",
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Remote screen",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(9f / 18f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_waiting_device),
                    contentDescription = "Waiting icon",
                    tint = Color(0xFF4B5563),
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 24.dp)
                )
                Text(
                    text = "Waiting for the other device to\nstart screen sharing...",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
