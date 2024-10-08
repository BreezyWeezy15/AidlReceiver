package com.app.lockcomposeAdmin


import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.core.content.ContextCompat

class RecentAppsAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "RecentAppsService"
    }

    private var lockedPackages: List<String> = emptyList()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "Event: $event")
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            val className = event.className?.toString() ?: return
            Log.d(TAG, "Class: $className")

            // Fetch locked packages before checking if the app should be locked
            fetchLockedPackages()

            if (isRecentAppsScreen(packageName, className)) {
                Toast.makeText(this, "Recent Apps opened", Toast.LENGTH_SHORT).show()
                triggerLockScreen(packageName)
            }
        }
    }

    private fun isRecentAppsScreen(packageName: String, className: String): Boolean {
        val recentAppsPackages = listOf(
            "com.android.systemui",      // Stock Android
            "com.samsung.android.systemui", // Samsung
            "com.google.android.systemui", // Pixel
            "com.oneplus.systemui",      // OnePlus
            "com.huawei.android.launcher", // Huawei
            "com.miui.home",             // Xiaomi
            "com.oppo.launcher"          // Oppo
        )

        val recentAppsClasses = listOf(
            "RecentsActivity",
            "RecentApplicationsActivity",
            "RecentAppsActivity",
            "RecentTasksActivity",
            "RecentAppActivity"
        )

        return recentAppsPackages.contains(packageName) && recentAppsClasses.any { className.contains(it, ignoreCase = true) }
    }

    override fun onInterrupt() {
        // Handle interruptions if needed
    }

    private fun triggerLockScreen(packageName: String) {
        // Check if the app is in the locked packages list
        if (lockedPackages.contains(packageName)) {
            val intent = Intent(this, LockScreenActivity::class.java).apply {
                putExtra("PACKAGE_NAME", packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ContextCompat.startActivity(this, intent, null)
        }
    }

    // Fetch locked packages from content provider
    private fun fetchLockedPackages() {
        val uri = Uri.parse("content://com.app.lockcomposeAdmin.provider/apps")
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            val packageNameIndex = it.getColumnIndex("packageName")
            val packages = mutableListOf<String>()
            while (it.moveToNext()) {
                val packageName = it.getString(packageNameIndex)
                packages.add(packageName)
            }
            lockedPackages = packages
        }
    }
}

