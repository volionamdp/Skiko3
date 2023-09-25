package com.volio.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun Context?.checkHasPermission(namePermission: String): Boolean {
    this?.let {
        return (ActivityCompat.checkSelfPermission(
            it,
            namePermission
        ) == PackageManager.PERMISSION_GRANTED)
    } ?: kotlin.run {
        return false
    }
}