package com.app.lockcomposeAdmin.services


import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.app.lockcomposeAdmin.R
import com.app.lockcomposeAdmin.helpers.AppDatabaseHelper

class RecentAppsAccessibilityService : AccessibilityService() {
    private lateinit var dbHelper: AppDatabaseHelper
    private var overlayView: View? = null
    private lateinit var windowManager: WindowManager
    private val unlockTimes = mutableMapOf<String, Long>()
    private val lockedApps = mutableSetOf<String>()

    companion object {
        private const val TAG = "RecentAppsService"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        val myPackageName = applicationContext.packageName

        if (packageName == myPackageName) return
        val lockedPackages = fetchLockedPackages()

        if (packageName in lockedPackages) {

            val unlockIntervalMinutes = getUnlockIntervalForApp(packageName)
            val lastUnlockTime = unlockTimes[packageName]
            if (lastUnlockTime != null && System.currentTimeMillis() - lastUnlockTime < unlockIntervalMinutes * 60 * 1000) {
                return
            }

            if (!lockedApps.contains(packageName)) {
                lockedApps.add(packageName)
                Toast.makeText(this, "Locked App Detected: $packageName", Toast.LENGTH_LONG).show()
                showPartialOverlay(packageName)
            }
        } else {
            checkAndRemoveExpiredApps()
        }
    }

    override fun onInterrupt() {
        // Handle interruptions if needed
    }

    private fun showPartialOverlay(packageName: String) {
        if (overlayView == null) {
            val layoutInflater = LayoutInflater.from(this)
            val overlayLayout = layoutInflater.inflate(R.layout.activity_lock_screen, null)
            val askPermissionBtn = overlayLayout.findViewById<Button>(R.id.askPermission)
            val cancelPermission = overlayLayout.findViewById<Button>(R.id.cancelPermission)
            val lockUi = overlayLayout.findViewById<LinearLayout>(R.id.lockUi)

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSPARENT
            )

            overlayView = overlayLayout
            windowManager.addView(overlayView, layoutParams)

            askPermissionBtn.setOnClickListener {
                if (lockUi.visibility == View.GONE) {
                    lockUi.visibility = View.VISIBLE
                    askPermissionBtn.visibility = View.GONE
                    cancelPermission.visibility = View.VISIBLE
                    showPassCodeUi(overlayLayout, packageName)
                }
            }

            cancelPermission.setOnClickListener {
                askPermissionBtn.visibility = View.VISIBLE
                cancelPermission.visibility = View.GONE
                lockUi.visibility = View.GONE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPassCodeUi(view: View, packageName: String) {
        val btn0 = view.findViewById<TextView>(R.id.btn0)
        val btn1 = view.findViewById<TextView>(R.id.btn1)
        val btn2 = view.findViewById<TextView>(R.id.btn2)
        val btn3 = view.findViewById<TextView>(R.id.btn3)
        val btn4 = view.findViewById<TextView>(R.id.btn4)
        val btn5 = view.findViewById<TextView>(R.id.btn5)
        val btn6 = view.findViewById<TextView>(R.id.btn6)
        val btn7 = view.findViewById<TextView>(R.id.btn7)
        val btn8 = view.findViewById<TextView>(R.id.btn8)
        val btn9 = view.findViewById<TextView>(R.id.btn9)
        val tick = view.findViewById<ImageView>(R.id.tick)

        val edit = view.findViewById<EditText>(R.id.passCodeEdit)

        val passcodeBuilder = StringBuilder()
        val numberButtons = listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)

        tick.setOnClickListener {
            val enteredPasscode = passcodeBuilder.toString()

            if (packageName.isNotEmpty()) {
                val correctPinCode = getPinCodeForApp(packageName)

                if (enteredPasscode == correctPinCode) {
                    unlockTimes[packageName] = System.currentTimeMillis()
                    lockedApps.remove(packageName)  // Remove the app from locked apps
                    edit.text.clear()
                    Toast.makeText(this, "Unlocked successfully", Toast.LENGTH_LONG).show()
                    removeOverlay()
                } else {
                    Toast.makeText(this, "Passcode is incorrect", Toast.LENGTH_LONG).show()
                }
            }
        }
        numberButtons.forEach { button ->
            button.setOnClickListener {
                passcodeBuilder.append(button.text)
                edit.setText(passcodeBuilder.toString())
            }
        }

        addRemoveIcon(edit)
        edit.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = edit.compoundDrawablesRelative[2]
                if (drawableEnd != null && event.rawX >= edit.right - drawableEnd.bounds.width()) {
                    if (passcodeBuilder.isNotEmpty()) {
                        passcodeBuilder.deleteCharAt(passcodeBuilder.length - 1)
                        edit.setText(passcodeBuilder.toString())
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun removeOverlay() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }

    private fun addRemoveIcon(edit: EditText) {
        val drawableEnd = edit.compoundDrawablesRelative[2]
        if (drawableEnd != null) {
            val greenColor = ContextCompat.getColor(this, R.color.greenColor)
            val colorFilter = PorterDuffColorFilter(greenColor, PorterDuff.Mode.SRC_IN)
            drawableEnd.colorFilter = colorFilter
            edit.invalidate()
        }
    }

    private fun getPinCodeForApp(packageName: String): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "apps",
            arrayOf("pin_code"),
            "package_name = ?",
            arrayOf(packageName),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow("pin_code"))
            }
        }
        return null
    }

    private fun getUnlockIntervalForApp(packageName: String): Int {
        val uri = Uri.parse("content://com.app.lockcomposeAdmin.provider/apps")
        var interval = 1  // Default unlock interval is 1 minute

        val cursor: Cursor? = contentResolver.query(
            uri,
            arrayOf("unlock_interval"),
            "package_name = ?",
            arrayOf(packageName),
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                interval = it.getInt(it.getColumnIndexOrThrow("unlock_interval"))
            }
        }
        return interval
    }

    private fun fetchLockedPackages(): List<String> {
        val uri = Uri.parse("content://com.app.lockcomposeAdmin.provider/apps")
        val lockedPackages = mutableListOf<String>()
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, arrayOf("package_name"), null, null, null)

            cursor?.use {
                val packageNameIndex = it.getColumnIndex("package_name")
                if (packageNameIndex == -1) {
                    Log.e(TAG, "'package_name' column not found")
                    return emptyList()
                }

                while (it.moveToNext()) {
                    val packageName = it.getString(packageNameIndex)
                    lockedPackages.add(packageName)
                }
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error reading from cursor: ${e.message}", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}", e)
        } finally {
            cursor?.close()
        }

        return lockedPackages
    }

    private fun checkAndRemoveExpiredApps() {
        val currentTime = System.currentTimeMillis()

        lockedApps.toList().forEach { packageName ->
            val unlockIntervalMinutes = getUnlockIntervalForApp(packageName)
            val lastUnlockTime = unlockTimes[packageName]
            if (lastUnlockTime != null && currentTime - lastUnlockTime >= unlockIntervalMinutes * 60 * 1000) {
                lockedApps.remove(packageName)
                Toast.makeText(this, "$packageName has been unlocked automatically.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


