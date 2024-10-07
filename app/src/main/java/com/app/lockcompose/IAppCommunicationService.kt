package com.app.lockcompose

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AppCommunicationService : Service() {

    // Binder given to clients
    private val binder = object : IAppCommunicationService.Stub() {
        override fun sendAppData(selectedAppPackages: MutableList<String>?, timeInterval: String?, pinCode: String?) {
            Log.d("AppCommunicationService", "Received App Packages: $selectedAppPackages")
            Log.d("AppCommunicationService", "Received Time Interval: $timeInterval")
            Log.d("AppCommunicationService", "Received PIN Code: $pinCode")

            // Handle the received data here and broadcast it to the UI
            handleAppData(selectedAppPackages, timeInterval, pinCode)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun handleAppData(appPackages: MutableList<String>?, timeInterval: String?, pinCode: String?) {
        if (appPackages != null && timeInterval != null && pinCode != null) {
            // Save data in SharedPreferences or notify UI directly via broadcast
            val sharedPref = getSharedPreferences("AppDataPrefs", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putStringSet("appPackages", appPackages.toSet())
            editor.putString("timeInterval", timeInterval)
            editor.putString("pinCode", pinCode)
            editor.apply()

            // Broadcast the data to the UI
            val intent = Intent("UPDATE_UI")
            intent.putExtra("appPackages", appPackages.toTypedArray())
            intent.putExtra("timeInterval", timeInterval)
            intent.putExtra("pinCode", pinCode)
            sendBroadcast(intent)

            Log.d("AppCommunicationService", "Broadcast sent to update UI")
        }
    }
}