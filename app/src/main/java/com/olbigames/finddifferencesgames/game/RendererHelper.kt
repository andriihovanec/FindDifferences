package com.olbigames.finddifferencesgames.game

interface RendererHelper {

    fun generateFboId(temp: IntArray): Int

    fun generateFboTexture(temp: IntArray): Int

    fun initGLES20(temp: IntArray, renderBufferId: Int, picW: Float, picH: Float, fboTexture: Int)
}