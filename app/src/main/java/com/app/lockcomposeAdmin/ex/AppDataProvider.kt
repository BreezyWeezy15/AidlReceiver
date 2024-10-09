package com.app.lockcomposeAdmin.ex

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log


class AppDataProvider : ContentProvider() {

    private lateinit var dbHelper: AppDatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = AppDatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        Log.d("AppDataProvider", "Query called on URI: $uri")
        return dbHelper.getAppsCursor()  // Your logic to fetch apps
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = dbHelper.insertApp(values)
        val insertUri = ContentUris.withAppendedId(uri, id)
        context?.contentResolver?.notifyChange(Uri.parse("content://com.app.lockcomposeAdmin.provider/apps"), null)
        return insertUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val rowsDeleted = dbHelper.deleteApp(selectionArgs?.firstOrNull())
        context?.contentResolver?.notifyChange(Uri.parse("content://com.app.lockcomposeAdmin.provider/apps"), null)
        return rowsDeleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        context?.contentResolver?.notifyChange(Uri.parse("content://com.app.lockcomposeAdmin.provider/apps"), null)
        return 0
    }

    override fun getType(uri: Uri): String? {
        return when (uri.pathSegments[0]) {
            "apps" -> "vnd.android.cursor.dir/vnd.com.app.lockcomposeAdmin.provider.apps"
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
    }
}