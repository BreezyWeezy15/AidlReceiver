
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.lockcomposeR.AppInfo
import com.app.lockcomposeR.R
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@SuppressLint("UnspecifiedRegisterReceiverFlag")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAppList() {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardBackgroundColor = if (isDarkTheme) Color.DarkGray else Color.White

    // MutableState for selected apps, time interval, and PIN code
    var selectedApps by remember { mutableStateOf<List<InstalledApps>>(emptyList()) }
    var timeInterval by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }

    // Register a broadcast receiver to update the UI when data is received
    DisposableEffect(Unit) {
        val updateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Get the data from the intent and update UI state
                val appList = intent?.getParcelableArrayListExtra<InstalledApps>("appPackages") ?: emptyList()
                val receivedTimeInterval = intent?.getStringExtra("timeInterval") ?: ""
                val receivedPinCode = intent?.getStringExtra("pinCode") ?: ""

                // Update state with received data
                selectedApps = appList
                timeInterval = receivedTimeInterval
                pinCode = receivedPinCode
            }
        }

        val filter = IntentFilter("UPDATE_UI")
        context.registerReceiver(updateReceiver, filter)

        onDispose {
            context.unregisterReceiver(updateReceiver)
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // LazyColumn to display the list of apps with their icons and time intervals
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(selectedApps) { app ->
                AppListItem(
                    app = app,
                    timeInterval = timeInterval,
                    onClick = { /* Handle app item click */ },
                    textColor = textColor,
                    cardBackgroundColor = cardBackgroundColor
                )
            }
        }
    }
}

@Composable
fun AppListItem(
    app: InstalledApps,
    timeInterval: String,
    onClick: () -> Unit,
    textColor: Color,
    cardBackgroundColor: Color
) {
    val iconBitmap = remember { app.icon?.toBitmap() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap.asImageBitmap(),
                    contentDescription = app.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                // Provide a default image if the icon is null
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background), // Replace with your placeholder resource
                    contentDescription = app.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                    color = textColor,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Interval: $timeInterval",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }
        }
    }
}

// Data class for the installed apps
@Parcelize
data class InstalledApps(
    val packageName: String,
    val name: String,
    val icon: @RawValue Drawable?
) : Parcelable

// Function to convert Drawable to Bitmap
fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}