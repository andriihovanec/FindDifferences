package com.olbigames.finddifferencesgames.extension

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment

fun Activity.checkIsSupportsEs2(): Boolean {
    val activityManager =
        getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val configurationInfo = activityManager.deviceConfigurationInfo
    return configurationInfo.reqGlEsVersion >= 0x20000 || Build.FINGERPRINT.startsWith("generic")
}