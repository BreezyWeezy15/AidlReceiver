package com.app.lockcomposeAdmin.ex

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
                $COLUMN_PIN_CODE TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade as needed
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_APPS")
        onCreate(db)
    }

    // Method to get a cursor containing all apps
    fun getAppsCursor(): Cursor {
        val db = readableDatabase
        return db.query(TABLE_APPS, null, null, null, null, null, null)
    }

    // Method to insert a new app
    fun insertApp(values: ContentValues?): Long {
        val db = writableDatabase
        return db.insert(TABLE_APPS, null, values) ?: -1
    }

    // Method to delete an app by package name
    fun deleteApp(packageName: String?): Int {
        // Open database for writing
        val db = this.writableDatabase
        return db.delete(TABLE_APPS, "package_name = ?", arrayOf(packageName))
    }
}