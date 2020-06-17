package com.olbigames.finddifferencesgames.extension

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showMessage(textResource: Int) {
    Toast.makeText(context, resources.getString(textResource), Toast.LENGTH_SHORT)
        .show()
}