package com.olbigames.finddifferencesgames.renderer.helper

object VerticesHelper {

    private var w = 0f
    private var h = 0f
    private var xOts = 0f
    private var yOts = 0f
    private const val lineSize = 10f

    fun calculatePicScale(screenHeight: Float, screenWidth: Float, picW: Float, picH: Float): Float {
        val picAspectRatio = (picW / picH)
        if (screenHeight < screenWidth) {
            val h1: Float = (screenHeight)
            val w1 = h1 * picAspectRatio
            if (w1 > (screenWidth - lineSize) / 2) {
                w = (screenWidth - lineSize) / 2
                h = w / picAspectRatio
                xOts = 0f
                yOts = (screenHeight - h) / 2
            } else {
                w = w1
                h = h1
                xOts = (screenWidth - lineSize) / 2 - w
                yOts = 0f
            }
        } else {
            val h1: Float = (screenHeight - 2 - lineSize) / 2
            val w1 = h1 * picAspectRatio
            if (w1 > screenWidth) {
                w = screenWidth
                h = w / picAspectRatio
                yOts = (screenHeight - 2 - lineSize) / 2 - h
                xOts = 0f
            } else {
                w = w1
                h = h1
                xOts = (screenWidth - w) / 2
                yOts = 0f
            }
        }

        return h / picH
    }

    fun verticesForMainBitmap(): FloatArray {
        return floatArrayOf(
                xOts, yOts + h, 0.0f,
                xOts, yOts, 0.0f,
                xOts + w, yOts, 0.0f,
                xOts + w, yOts + h, 0.0f
            )
    }

    fun verticesForDifferentBitmap(screenHeight: Float, screenWidth: Float): FloatArray {
        return if (screenHeight < screenWidth) {
            floatArrayOf(
                screenWidth - xOts - w, yOts + h, 0.0f,
                screenWidth - xOts - w, yOts, 0.0f,
                screenWidth - xOts, yOts, 0.0f,
                screenWidth - xOts, yOts + h, 0.0f
            )
        } else {
            floatArrayOf(
                xOts, screenHeight - 2 - yOts, 0.0f,
                xOts, screenHeight - 2 - h - yOts, 0.0f,
                xOts + w, screenHeight - 2 - h - yOts, 0.0f,
                xOts + w, screenHeight - 2 - yOts, 0.0f
            )
        }
    }

    fun getVertices3(): FloatArray {
        return floatArrayOf(
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f
        )
    }

    fun getVertices4(picW: Float, picH: Float): FloatArray {
        return floatArrayOf(
            0f, 0f, 0.0f,
            0f, picH, 0.0f,
            picW, picH, 0.0f,
            picW, 0f, 0.0f
        )
    }

    fun getVertices5(picW: Float, picH: Float): FloatArray {
        return floatArrayOf(
            0f, 0f, 0.0f,
            0f, picH, 0.0f,
            picW, picH, 0.0f,
            picW, 0f, 0.0f
        )
    }

    fun getVertices6(): FloatArray {
        return floatArrayOf(
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f,
            -1f, 1f, 0.0f
        )
    }

    fun getVertices7(): FloatArray {
        return floatArrayOf(
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f
        )
    }
}