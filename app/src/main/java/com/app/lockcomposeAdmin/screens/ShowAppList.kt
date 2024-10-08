import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.app.lockcomposeAdmin.AppLockManager
import com.app.lockcomposeAdmin.R
import com.app.lockcomposeAdmin.models.InstalledApps

@Composable
fun ShowAppList() {
    val context = LocalContext.current
    val appLockManager = remember { AppLockManager(context) }
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardBackgroundColor = if (isDarkTheme) Color.DarkGray else Color.White

    var selectedApps by remember { mutableStateOf<List<InstalledApps>>(emptyList()) }
    var timeInterval by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }

    // Fetch data from the ContentProvider
    fun fetchDataFromContentProvider() {
        val contentResolver = context.contentResolver
        val uri = Uri.parse("content://com.app.lockcomposeAdmin.provider/apps")
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            val apps = mutableListOf<InstalledApps>()
            while (it.moveToNext()) {
                val packageName = it.getString(it.getColumnIndexOrThrow("package_name"))
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val iconByteArray = it.getBlob(it.getColumnIndexOrThrow("icon"))
                val interval = it.getString(it.getColumnIndexOrThrow("interval"))
                val pin = it.getString(it.getColumnIndexOrThrow("pin_code"))

                // Convert byte array to Bitmap and then to Drawable
                val iconBitmap = BitmapFactory.decodeByteArray(iconByteArray, 0, iconByteArray.size)
                val iconDrawable = BitmapDrawable(context.resources, iconBitmap)

                // Add to the apps list
                apps.add(InstalledApps(packageName, name, iconDrawable))

                // Save the package name to the AppLockManager
                appLockManager.addPackage(setOf(packageName))

                // Set interval and pin code (assuming they are the same for all apps)
                timeInterval = interval
                pinCode = pin
            }

            // Update state and recompose
            selectedApps = apps
        }
    }

    // Fetch data from ContentProvider on first composition or after every insertion
    LaunchedEffect(Unit) {
        fetchDataFromContentProvider()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
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

        Spacer(modifier = Modifier.height(16.dp))

        // Display the PIN code
        Text(
            text = "PIN Code: $pinCode",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
            color = textColor
        )
    }
}

// Composable for rendering each app in the list
@Composable
fun AppListItem(
    app: InstalledApps,  // Data class containing app details
    timeInterval: String, // Interval to show under the app name
    onClick: () -> Unit,  // Handle item clicks
    textColor: Color,     // Color for the text
    cardBackgroundColor: Color // Background color for the card
) {
    // Convert Drawable to Bitmap for displaying the app icon
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
            // Show the app icon
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
                    painter = painterResource(R.drawable.ic_launcher_background), // Replace with a placeholder resource
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