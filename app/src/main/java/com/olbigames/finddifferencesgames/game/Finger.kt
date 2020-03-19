package com.olbigames.finddifferencesgames.game

import android.graphics.Point

class Finger(id: Int, x: Int, y: Int) {
    // Finger id
    var ID: Int
    var pointNow: Point
    var pointBefore: Point
    var wasDown: Long = System.currentTimeMillis()
    // Has the movement been already made
    var enabled = false

    init {
        ID = id
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