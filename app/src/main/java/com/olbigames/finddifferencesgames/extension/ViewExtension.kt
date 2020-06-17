package com.olbigames.finddifferencesgames.extension

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.utilities.Constants.REFERENCE_POINT_20

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.GONE
}

fun ImageView.loadFromUrl(url: String) =
    Glide.with(this.context.applicationContext)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)

fun ImageView.loadFromDrawable(resource: Drawable) =
    Glide.with(this.context.applicationContext)
        .load(resource)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)

fun ImageView.setCorrectImage(position: Int, game: GameEntity) {
    if (position <= REFERENCE_POINT_20) {
        val resourceId: Int =
            context.resources.getIdentifier(game.pathToMainFile, "raw", context.packageName)
        setImageResource(resourceId)
    } else {
        setImageURI(Uri.parse(game.pathToMainFile))
    }
}