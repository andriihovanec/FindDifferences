package com.olbigames.finddifferencesgames.renderer

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GameGLSurfaceView(
    context: Context?,
    attrs: AttributeSet?
) : GLSurfaceView(context, attrs) {

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
    }
}