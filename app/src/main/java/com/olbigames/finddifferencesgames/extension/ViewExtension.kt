package com.olbigames.finddifferencesgames.extension

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun View.visible() { this.visibility = View.VISIBLE }

fun View.invisible() { this.visibility = View.GONE }

fun ImageView.loadFromUrl(url: String) =
    Glide.with(this.context.applicationContext)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)