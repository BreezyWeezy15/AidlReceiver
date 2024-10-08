package com.app.lockcomposeAdmin

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class AppInfo(
    val packageName: String,
    val appName: String,
    val appIcon: ByteArray
) : Parcelable 