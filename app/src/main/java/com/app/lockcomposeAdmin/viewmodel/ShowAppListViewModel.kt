package com.app.lockcomposeAdmin.viewmodel

import InstalledApps
import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.lockcomposeAdmin.helpers.AppDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ShowAppListViewModel(private val application: Application) : ViewModel() {

    private val _selectedAppsLiveData = MutableLiveData<List<InstalledApps>>()
    val selectedAppsLiveData: LiveData<List<InstalledApps>> = _selectedAppsLiveData

    private val dbHelper = AppDatabaseHelper(application)
    private var job: Job? = null

    init {
        startPolling()
    }

    private fun startPolling() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                loadAppsFromDatabase()
            }
        }
    }
    private fun loadAppsFromDatabase() {
        val apps = mutableListOf<InstalledApps>()
        val cursor = dbHelper.getAppsCursor()
        cursor.use {
            while (it.moveToNext()) {
                val packageName = it.getString(it.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PACKAGE_NAME))
                val name = it.getString(it.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_NAME))
                val iconByteArray = it.getBlob(it.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_ICON))
                val interval = it.getString(it.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_INTERVAL))
                val pinCode = it.getString(it.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PIN_CODE))

                val iconBitmap = BitmapFactory.decodeByteArray(iconByteArray, 0, iconByteArray.size)
                val iconDrawable = BitmapDrawable(application.resources, iconBitmap)

                apps.add(InstalledApps(packageName, name, iconDrawable, interval, pinCode))
            }
        }
        _selectedAppsLiveData.postValue(apps)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}