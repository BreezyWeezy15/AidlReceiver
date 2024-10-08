package com.app.lockcomposeAdmin.ex

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "apps.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE apps (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                package_name TEXT,
                name TEXT,
                icon BLOB,
                interval TEXT,
                pin_code TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS apps")
        onCreate(db)
    }
}