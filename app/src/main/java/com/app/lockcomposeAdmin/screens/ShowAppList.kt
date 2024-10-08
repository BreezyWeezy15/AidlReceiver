import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.app.lockcomposeAdmin.AppLockService

@Composable
fun ShowAppList() {
    val context = LocalContext.current
    var selectedApps by remember { mutableStateOf<List<InstalledApps>>(emptyList()) }


    LaunchedEffect(Unit) {
        val serviceIntent = Intent(context, AppLockService::class.java)
        context.startService(serviceIntent)
    }

    // Create and register ContentObserver
    val contentObserver = remember {
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(self: Boolean) {
                super.onChange(self)
                fetchDataFromContentProvider(context) { apps ->
                    selectedApps = apps // Update the app list
                }
            }
        }
    }

    DisposableEffect(Unit) {
        context.contentResolver.registerContentObserver(
            Uri.parse("content://com.app.lockcomposeAdmin.provider/apps"),
            true,
            contentObserver
        )
        onDispose {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }

    // Initial data fetch
    LaunchedEffect(Unit) {
        fetchDataFromContentProvider(context) { apps ->
            selectedApps = apps
        }
    }

    // UI to display apps
    LazyColumn {
        items(selectedApps) { app ->
            AppListItem(
                app = app,
                interval = app.interval,
                pinCode = app.pinCode
            ) {
                // Unlock app logic here
            }
        }
    }
}

// Function to fetch data from ContentProvider
fun fetchDataFromContentProvider(
    context: Context,
    onDataFetched: (List<InstalledApps>) -> Unit
) {
    val contentResolver = context.contentResolver
    val uri = Uri.parse("content://com.app.lockcomposeAdmin.provider/apps")
    val cursor = contentResolver.query(uri, null, null, null, null)

    cursor?.use {
        val apps = mutableListOf<InstalledApps>()
        while (it.moveToNext()) {
            val packageName = it.getString(it.getColumnIndexOrThrow("package_name"))
            val name = it.getString(it.getColumnIndexOrThrow("name"))
            val iconByteArray = it.getBlob(it.getColumnIndexOrThrow("icon"))
            val interval = it.getString(it.getColumnIndexOrThrow("interval")) // Fetch interval
            val pinCode = it.getString(it.getColumnIndexOrThrow("pin_code")) // Fetch pinCode

            // Convert byte array to Bitmap and then to Drawable
            val iconBitmap = BitmapFactory.decodeByteArray(iconByteArray, 0, iconByteArray.size)
            val iconDrawable = BitmapDrawable(context.resources, iconBitmap)

            // Create InstalledApps object with interval and pinCode
            apps.add(InstalledApps(packageName, name, iconDrawable, interval, pinCode))
        }
        onDataFetched(apps) // Update state via callback
    }
}

// Composable to display each app with interval and pin code
@Composable
fun AppListItem(app: InstalledApps, interval: String, pinCode: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp) // Rounded card corners
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = app.icon!!.toBitmap().asImageBitmap(),
                    contentDescription = app.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape) // Make the icon circular
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) // Add a border
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interval and Pin Code Section
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Interval: $interval" + "Min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pin Code: $pinCode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

data class InstalledApps(
    val packageName: String,
    val name: String,
    val icon: Drawable?,
    val interval: String, // Add interval
    val pinCode: String    // Add pinCode
)