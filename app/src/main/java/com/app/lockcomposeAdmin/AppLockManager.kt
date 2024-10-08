package com.app.lockcomposeAdmin

import android.content.Context
import android.content.SharedPreferences

class AppLockManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppLockPrefs", Context.MODE_PRIVATE)

    fun getSelectedPackages(): Set<String> {
        return sharedPreferences.getStringSet("selected_package_names", emptySet()) ?: emptySet()
    }

    fun addPackage(packageName: String) {
        val selectedPackages = getSelectedPackages().toMutableSet()
        selectedPackages.add(packageName)
        with(sharedPreferences.edit()) {
            putStringSet("selected_package_names", selectedPackages)
            apply()
        }
    }

    fun removePackage(packageName: String) {
        val selectedPackages = getSelectedPackages().toMutableSet()
        selectedPackages.remove(packageName)
        with(sharedPreferences.edit()) {
            putStringSet("selected_package_names", selectedPackages)
            apply()
        }
    }

    // Optionally manage the access list
    fun updateAccessList(packageName: String) {
        val accessList = sharedPreferences.getStringSet("access_list", emptySet())?.toMutableSet() ?: mutableSetOf()
        accessList.remove(packageName)
        with(sharedPreferences.edit()) {
            putStringSet("access_list", accessList)
            apply()
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