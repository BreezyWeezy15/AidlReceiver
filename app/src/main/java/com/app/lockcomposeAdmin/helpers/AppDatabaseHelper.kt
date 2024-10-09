package com.app.lockcomposeAdmin.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_lock.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_APPS = "apps"

        // Column names
        const val COLUMN_ID = "_id"
        const val COLUMN_PACKAGE_NAME = "package_name"
        const val COLUMN_NAME = "name"
        const val COLUMN_ICON = "icon"
        const val COLUMN_INTERVAL = "interval"
        const val COLUMN_PIN_CODE = "pin_code"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSQL = """
            CREATE TABLE $TABLE_APPS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PACKAGE_NAME TEXT NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_ICON BLOB,
                $COLUMN_INTERVAL TEXT,
                $COLUMN_PIN_CODE TEXT,
                UNIQUE($COLUMN_PACKAGE_NAME, $COLUMN_NAME) ON CONFLICT REPLACE
            )
        """.trimIndent()
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_APPS")
        onCreate(db)
    }


    fun getAppsCursor(): Cursor {
        val db = readableDatabase
        return db.query(TABLE_APPS, null, null, null, null, null, null)
    }

    fun insertApp(values: ContentValues?): Long {
        val db = writableDatabase
        val packageName = values?.getAsString(COLUMN_PACKAGE_NAME)
        val appName = values?.getAsString(COLUMN_NAME)

        // Check if an entry with the same package name and app name already exists
        val existingAppCursor = db.query(
            TABLE_APPS,
            arrayOf(COLUMN_ID),
            "$COLUMN_PACKAGE_NAME = ? AND $COLUMN_NAME = ?",
            arrayOf(packageName, appName),
            null,
            null,
            null
        )

        // If a duplicate exists, remove it
        if (existingAppCursor.moveToFirst()) {
            val appId = existingAppCursor.getLong(existingAppCursor.getColumnIndexOrThrow(COLUMN_ID))
            db.delete(TABLE_APPS, "$COLUMN_ID = ?", arrayOf(appId.toString()))
        }
        existingAppCursor.close()

        // Insert the new app
        return db.insert(TABLE_APPS, null, values) ?: -1
    }

    fun deleteApp(packageName: String?): Int {
        val db = writableDatabase
        return db.delete(TABLE_APPS, "$COLUMN_PACKAGE_NAME = ?", arrayOf(packageName))
    }
}