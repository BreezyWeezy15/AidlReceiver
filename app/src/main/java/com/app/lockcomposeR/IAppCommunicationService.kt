package com.app.lockcomposeR

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AppCommunicationService : Service() {

    // Binder given to clients
    private val binder = object : IAppCommunicationService.Stub() {
        override fun sendAppData(apps: MutableList<AppInfo>?, timeInterval: String?, pinCode: String?) {
            Log.d("AppCommunicationService", "Received App Packages: $apps")
            Log.d("AppCommunicationService", "Received Time Interval: $timeInterval")
            Log.d("AppCommunicationService", "Received PIN Code: $pinCode")

            // Handle the received data here and broadcast it to the UI
            handleAppData(apps, timeInterval, pinCode)
        }



    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun handleAppData(apps: MutableList<AppInfo>?, timeInterval: String?, pinCode: String?) {
        if (apps != null && timeInterval != null && pinCode != null) {
            // Broadcast the data to the UI
            val intent = Intent("UPDATE_UI")
            intent.putParcelableArrayListExtra("appList", ArrayList(apps))
            intent.putExtra("timeInterval", timeInterval)
            intent.putExtra("pinCode", pinCode)
            sendBroadcast(intent)

            Log.d("AppCommunicationService", "Broadcast sent to update UI")
        }
    }
}