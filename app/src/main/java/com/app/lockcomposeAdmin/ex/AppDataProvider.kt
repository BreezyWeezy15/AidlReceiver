package com.app.lockcomposeAdmin.ex

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import com.app.lockcomposeAdmin.AppLockManager


class AppDataProvider : ContentProvider() {

    private lateinit var dbHelper: AppDatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = AppDatabaseHelper(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return dbHelper.getAppsCursor()  // Your logic to fetch apps
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = dbHelper.insertApp(values)
        context?.contentResolver?.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // Check if the URI matches your specific app deletion path
        if (uri.pathSegments[0] == "apps") {
            val packageNameToDelete = selectionArgs?.firstOrNull()
            val rowsDeleted = deleteAppFromDatabase(packageNameToDelete)
            context?.contentResolver?.notifyChange(uri, null)
            return rowsDeleted
        }
        throw IllegalArgumentException("Unknown URI $uri")
    }

    // Implement the logic to delete the app from the database
    private fun deleteAppFromDatabase(packageName: String?): Int {
        // Your logic to delete the app from the database
        return dbHelper.deleteApp(packageName)
    }
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0  // Your update logic if needed
    }

    override fun getType(uri: Uri): String? {
        return when (uri.pathSegments[0]) {
            "apps" -> "vnd.android.cursor.dir/vnd.com.app.lockcomposeAdmin.provider.apps"
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
    }


}