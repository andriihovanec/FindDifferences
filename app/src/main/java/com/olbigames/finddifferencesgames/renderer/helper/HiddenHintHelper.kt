package com.olbigames.finddifferencesgames.renderer.helper

import android.util.Log
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random

class HiddenHintHelper(
    private val displayWidth: Float,
    private val displayHeight: Float,
    val founded: Boolean,
    var hintCoordinateAxisX: Float,
    var hintCoordinateAxisY: Float,
    private val pictureScale: Float
) {

    private var speedX = 0f
    private var speedY = 0f
    private var vector1 = 0f
    private var vector2 = 0f

    var timeLeft = 3000.0f
    var animShowing = false
    private val mScreenHeight = 0f
    private var scaleNow = 0f
    private var scsc = 0f
    val level = 1
    private var xLeft = false
    private var yAbove = false
    private var X = 0f
    private var Y = 0f

    init {
        xLeft = displayWidth > hintCoordinateAxisX
        yAbove = displayHeight > hintCoordinateAxisY
        scaleNow = pictureScale
        scsc = Random.nextFloat() * 360.0f
        val sc0: Float = Random.nextFloat() * 25.0f
        speedX = (sin(scsc.toDouble()) * sc0).toFloat()
        speedY = (cos(scsc.toDouble()) * sc0).toFloat()
        vector1 = speedX + hintCoordinateAxisX
        vector2 = mScreenHeight - speedY + hintCoordinateAxisX
        X = hintCoordinateAxisX
        Y = hintCoordinateAxisY
    }

    fun startAnim() {
        animShowing = true
    }

    fun getTime(): Float {
        return 3000.0f - timeLeft
    }

    fun col(): Int {
        return 200
    }

    fun getScale(): Float {
        return scaleNow
    }

    fun getVector2(time: Float) {
        val rotationSpeed = 2.5f
        if (speedY > 0.0f) {
            speedY -= 1.0f * time
        } else {
            speedY += 1.0f * time
        }
        if (speedX > 0.0f) {
            speedX -= 1.0f * time
        } else {
            speedX += 1.0f * time
        }
        val dfs = floor(timeLeft / 15.0f.toDouble()).toInt()
        if (dfs < level && level < dfs + 20.0) {
            X = hintCoordinateAxisX
            Y = hintCoordinateAxisY
        }
        scsc += time
        vector1 = (X - speedX + 10.0 * sin(
            rotationSpeed * scsc.toDouble()
        )).toFloat()
        vector2 =
            mScreenHeight - (Y - speedY + 10.0 * cos(
                rotationSpeed * scsc.toDouble()
            )).toFloat()
    }

    fun timeAdd(time: Float): Boolean {
        var a = 3.0f
        val sX = (displayWidth - hintCoordinateAxisX) / 10000.0f
        val sY = (displayHeight - hintCoordinateAxisY) / 10000.0f

        timeLeft -= time
        if (timeLeft < 0.0f) {
            animShowing = false
            Log.d("HIDDEN_HINT", "timeLeft < 0, animShowing = false")
            return true
        }
        if (
            xLeft && hintCoordinateAxisX > displayWidth ||
            !xLeft && displayWidth > hintCoordinateAxisX ||
            yAbove && hintCoordinateAxisY > displayHeight ||
            !yAbove && displayHeight > hintCoordinateAxisY
        ) {
            hintCoordinateAxisX = 10000.0f
            hintCoordinateAxisY = 10000.0f
            Log.d("HIDDEN_HINT", "" + hintCoordinateAxisX + ", y = " + hintCoordinateAxisY)
            return true
        }
        if (timeLeft > 2500.0f) {
            val maxScale = 4.0f
            scaleNow = maxScale + (pictureScale - maxScale) * ((timeLeft - 2500.0f) / 500.0f)
            Log.d("HIDDEN_HINT", "timeLeft > 2500.0f, scalenow = $scaleNow")
        }
        if (timeLeft < 2200.0f) {
            if (a < 12) {
                a *= 1.1f
            }
            hintCoordinateAxisX += (a * sX * time).toInt()
            hintCoordinateAxisY += (a * sY * time).toInt()
            Log.d("HIDDEN_HINT", "timeLeft < 2200.0f, x = " + hintCoordinateAxisX + "y = " + hintCoordinateAxisY)
        }
        getVector2(time / 1000.0f)
        return false
    }
}