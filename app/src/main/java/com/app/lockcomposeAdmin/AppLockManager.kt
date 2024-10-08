package com.app.lockcomposeAdmin

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.app.lockcomposeAdmin.ex.AppDatabaseHelper

class AppLockManager(context: Context) {
    private val dbHelper: AppDatabaseHelper = AppDatabaseHelper(context)
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppLockPrefs", Context.MODE_PRIVATE)

    fun getSelectedPackages(): Set<String> {
        return sharedPreferences.getStringSet("selected_package_names", emptySet()) ?: emptySet()
    }


    fun removePackage(packageName: String) {

        // Then, remove the package from the SQLite database
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("apps", "package_name = ?", arrayOf(packageName))

        if (rowsDeleted > 0) {
            Log.d("AppLockManager", "Successfully deleted app: $packageName")
        } else {
            Log.e("AppLockManager", "Failed to delete app: $packageName")
        }
    }


    fun removePackageFromAccessList(packageName: String) {
        val accessList = sharedPreferences.getStringSet("access_list", emptySet())?.toMutableSet() ?: mutableSetOf()
        accessList.remove(packageName)
        with(sharedPreferences.edit()) {
            putStringSet("access_list", accessList)
            apply()
        }
    }
}