package com.olbigames.finddifferencesgames.extension

import androidx.navigation.NavOptions
import com.olbigames.finddifferencesgames.R

// animation options
fun animateFade(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.fragment_fade_enter)
        .setExitAnim(R.anim.fragment_fade_exit)
        .setPopEnterAnim(R.anim.fragment_fade_enter)
        .setPopExitAnim(R.anim.fragment_fade_exit)
        .build()
}

// animation options
fun animateAndPopFromStack(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.fragment_fade_enter)
        .setExitAnim(R.anim.fragment_fade_exit)
        .setPopEnterAnim(R.anim.fragment_fade_enter)
        .setPopExitAnim(R.anim.fragment_fade_exit)
        .setPopUpTo(
            R.id.gameFragment,
            true
        ).build()
}