package com.app.lockcomposeAdmin.models

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class InstalledApps(
    val packageName: String,
    val name: String,
    val icon: @RawValue Drawable?
) : Parcelable