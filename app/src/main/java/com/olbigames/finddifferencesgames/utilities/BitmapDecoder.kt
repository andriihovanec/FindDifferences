package com.olbigames.finddifferencesgames.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.olbigames.finddifferencesgames.MainActivity

fun getBitmapsForGame(level: Int, mainFile: String, differenceFile: String): List<Bitmap> {
    val list = mutableListOf<Bitmap>()
    return if (level <= 20) {
        list.add(getBitmapFromResource(
            MainActivity.getContext(),
            mainFile
        ))
        list.add(getBitmapFromResource(
            MainActivity.getContext(),
            differenceFile
        ))
        list
    } else {
        list.add(BitmapFactory.decodeFile(mainFile))
        list.add(BitmapFactory.decodeFile(differenceFile))
        list
    }
}

private fun getBitmapFromResource(context: Context, resourceName: String): Bitmap {
    val dimensions = BitmapFactory.Options()
    dimensions.inPreferredConfig = Bitmap.Config.ARGB_8888
    dimensions.inScaled = false

    val id: Int = context.resources.getIdentifier(resourceName, "raw", context.packageName)
    return BitmapFactory.decodeResource(context.resources, id, dimensions)
}