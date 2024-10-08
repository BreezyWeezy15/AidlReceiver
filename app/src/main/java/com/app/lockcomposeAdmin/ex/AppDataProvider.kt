package com.app.lockcomposeAdmin.ex

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log

class AppDataProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.app.lockcomposeAdmin.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/apps")
        const val URI_CODE_APPS = 1

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "apps", URI_CODE_APPS)
        }
    }

    private lateinit var dbHelper: AppDatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = AppDatabaseHelper(context!!)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            URI_CODE_APPS -> {
                val db = dbHelper.writableDatabase
                val id = db.insert("apps", null, values)

                if (id != -1L) {
                    context?.contentResolver?.notifyChange(uri, null)
                    return ContentUris.withAppendedId(CONTENT_URI, id)
                }
            }
        }
        return null
    }

    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? {
        when (uriMatcher.match(uri)) {
            URI_CODE_APPS -> {
                val db = dbHelper.readableDatabase
                return db.query(
                    "apps",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
        }
        return null
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        return db.update("apps", values, selection, selectionArgs)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        return db.delete("apps", selection, selectionArgs)
    }

    override fun getType(uri: Uri): String? = null
}