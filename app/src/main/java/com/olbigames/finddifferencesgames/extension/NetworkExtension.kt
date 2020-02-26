package com.olbigames.finddifferencesgames.extension

import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.Fragment

fun Fragment.checkCurrentConnection(): Boolean {
    val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.isActiveNetworkMetered
}