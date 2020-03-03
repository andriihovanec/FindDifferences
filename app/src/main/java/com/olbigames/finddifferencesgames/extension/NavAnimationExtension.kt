package com.olbigames.finddifferencesgames.extension

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.olbigames.finddifferencesgames.R

// animation options
fun Fragment.animateFade(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.fragment_fade_enter)
        .setExitAnim(R.anim.fragment_fade_exit)
        .setPopEnterAnim(R.anim.fragment_fade_enter)
        .setPopExitAnim(R.anim.fragment_fade_exit)
        .build()
}