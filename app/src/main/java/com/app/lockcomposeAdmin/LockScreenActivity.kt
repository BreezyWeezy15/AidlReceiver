package com.app.lockcomposeAdmin


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.lockcomposeAdmin.ex.AppDatabaseHelper


class LockScreenActivity : AppCompatActivity() {

    private lateinit var lockUi: LinearLayout
    private lateinit var askPermissionBtn: Button
    private lateinit var dbHelper: AppDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        lockUi = findViewById(R.id.lockUi)
        askPermissionBtn = findViewById(R.id.askPermission)
        askPermissionBtn.setOnClickListener {
            if (lockUi.visibility == View.GONE) {
                lockUi.visibility = View.VISIBLE
                showPassCodeUi()
            }
        }


        dbHelper = AppDatabaseHelper(this)
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun showPassCodeUi() {
        val btn0 = findViewById<TextView>(R.id.btn0)
        val btn1 = findViewById<TextView>(R.id.btn1)
        val btn2 = findViewById<TextView>(R.id.btn2)
        val btn3 = findViewById<TextView>(R.id.btn3)
        val btn4 = findViewById<TextView>(R.id.btn4)
        val btn5 = findViewById<TextView>(R.id.btn5)
        val btn6 = findViewById<TextView>(R.id.btn6)
        val btn7 = findViewById<TextView>(R.id.btn7)
        val btn8 = findViewById<TextView>(R.id.btn8)
        val btn9 = findViewById<TextView>(R.id.btn9)
        val tick = findViewById<ImageView>(R.id.tick)
        val edit = findViewById<EditText>(R.id.passCodeEdit)

        val passcodeBuilder = StringBuilder()
        val numberButtons = listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)

        tick.setOnClickListener {
            val enteredPasscode = passcodeBuilder.toString()
            val packageName = intent.getStringExtra("PACKAGE_NAME")

            if (packageName != null) {
                val correctPinCode = getPinCodeForApp(packageName)

                if (enteredPasscode == correctPinCode) {
                    edit.text.clear()
                    removePackage(packageName)
                    finishAffinity()
                } else {
                    Toast.makeText(this, "Passcode is incorrect", Toast.LENGTH_LONG).show()
                }
            }
        }

        numberButtons.forEach { button ->
            button.setOnClickListener {
                passcodeBuilder.append(button.text)
                edit.setText(passcodeBuilder.toString())
            }
        }

        addRemoveIcon(edit)
        edit.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = edit.compoundDrawablesRelative[2]
                if (drawableEnd != null && event.rawX >= edit.right - drawableEnd.bounds.width()) {
                    if (passcodeBuilder.isNotEmpty()) {
                        passcodeBuilder.deleteCharAt(passcodeBuilder.length - 1)
                        edit.setText(passcodeBuilder.toString())
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
    private fun addRemoveIcon(edit: EditText) {
        val greenColor = ContextCompat.getColor(this, R.color.greenColor)
        val colorFilter = PorterDuffColorFilter(greenColor, PorterDuff.Mode.SRC_IN)
        edit.compoundDrawablesRelative[2]?.colorFilter = colorFilter
    }
    private fun getPinCodeForApp(packageName: String): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "apps",
            arrayOf("pin_code"),
            "package_name = ?",
            arrayOf(packageName),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow("pin_code"))
            }
        }
        return null
    }
    private fun removePackage(packageName: String) {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("apps", "package_name = ?", arrayOf(packageName))
    }
}