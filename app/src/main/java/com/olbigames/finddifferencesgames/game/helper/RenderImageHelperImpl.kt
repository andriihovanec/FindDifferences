package com.olbigames.finddifferencesgames.game.helper

import android.opengl.GLES20

class RenderImageHelperImpl :
    RenderImageHelper {

    var fboId: Int = 0
    var fboTexture: Int = 0
    var fboId2: Int = 0
    var fboTexture2: Int = 0

    // в этот массив OpenGL ES запишет свободный номер текстуры,
    // который называют именем текстуры textureIds
    var temp = IntArray(1)

    override fun initGLES20MainImage(
        picW: Float,
        picH: Float
    ) {

        //generate fbo id
        GLES20.glGenFramebuffers(1, temp, 0)
        fboId = temp[0]

        //generate texture
        GLES20.glGenTextures(1, temp, 0)
        fboTexture = temp[0]

        //generate render buffer
        GLES20.glGenRenderbuffers(1, temp, 0)
        val renderBufferId = temp[0]

        //Bind Frame buffer
        GLES20.glBindFramebuffer(
            GLES20.GL_FRAMEBUFFER,
            fboId
        )
        //Bind texture
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            fboTexture
        )
        //Define texture parameters
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            picW.toInt(),
            picH.toInt(),
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(
            GLES20.GL_RENDERBUFFER,
            renderBufferId
        )
        GLES20.glRenderbufferStorage(
            GLES20.GL_RENDERBUFFER,
            GLES20.GL_DEPTH_COMPONENT16,
            picW.toInt(),
            picH.toInt()
        )
        //Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            fboTexture,
            0
        )
        //Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_DEPTH_ATTACHMENT,
            GLES20.GL_RENDERBUFFER,
            renderBufferId
        )
        //we are done, reset
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            0
        )
        GLES20.glBindRenderbuffer(
            GLES20.GL_RENDERBUFFER,
            0
        )
        GLES20.glBindFramebuffer(
            GLES20.GL_FRAMEBUFFER,
            0
        )
    }

    override fun initGLES20DifferentImage(picW: Float, picH: Float) {
        temp = IntArray(1)

        //generate fbo id
        GLES20.glGenFramebuffers(1, temp, 0)
        fboId2 = temp[0]

        //generate texture
        GLES20.glGenTextures(1, temp, 0)
        fboTexture2 = temp[0]

        //generate render buffer
        GLES20.glGenRenderbuffers(1, temp, 0)
        val renderBufferId2 = temp[0]

        //Bind Frame buffer
        GLES20.glBindFramebuffer(
            GLES20.GL_FRAMEBUFFER,
            fboId2
        )
        //Bind texture
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            fboTexture2
        )
        //Define texture parameters
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            picW.toInt(),
            picH.toInt(),
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(
            GLES20.GL_RENDERBUFFER,
            renderBufferId2
        )
        GLES20.glRenderbufferStorage(
            GLES20.GL_RENDERBUFFER,
            GLES20.GL_DEPTH_COMPONENT16,
            picW.toInt(),
            picH.toInt()
        )
        //Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            fboTexture2,
            0
        )
        //Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_DEPTH_ATTACHMENT,
            GLES20.GL_RENDERBUFFER,
            renderBufferId2
        )
        //we are done, reset
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            0
        )
        GLES20.glBindRenderbuffer(
            GLES20.GL_RENDERBUFFER,
            0
        )
        GLES20.glBindFramebuffer(
            GLES20.GL_FRAMEBUFFER,
            0
        )
    }
}