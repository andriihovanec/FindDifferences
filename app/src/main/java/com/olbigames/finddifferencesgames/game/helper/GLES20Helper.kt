package com.olbigames.finddifferencesgames.game.helper

interface GLES20Helper {

    fun initGLES20MainImage(
        picW: Float,
        picH: Float
    )

    fun initGLES20DifferentImage(
        picW: Float,
        picH: Float
    )

    fun createTextureTransparency()

    fun createShadersImages()

    fun createShadersPoint()

    fun setupViewGLES20()

    fun clearScreenAndDepthBuffer()
}