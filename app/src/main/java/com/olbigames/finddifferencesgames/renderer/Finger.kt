package com.olbigames.finddifferencesgames.renderer

import android.graphics.Point

class Finger(x: Int, y: Int) {
    var pointNow: Point
    var pointBefore: Point
    var enabled = false

    init {
        pointBefore = Point(x, y)
        pointNow = pointBefore
    }

    fun setNow(x: Int, y: Int) {
        if (!enabled) {
            enabled = true
            pointBefore = Point(x, y)
            pointNow = pointBefore
        } else {
            pointBefore = pointNow
            pointNow = Point(x, y)
        }
    }
}