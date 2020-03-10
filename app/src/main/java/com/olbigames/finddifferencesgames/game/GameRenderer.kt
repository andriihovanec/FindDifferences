package com.olbigames.finddifferencesgames.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.SoundPool
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.olbigames.finddifferencesgames.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(
    val context: Context,
    private val viewModelScope: CoroutineScope,
    private var screenWidth: Int,
    private var screenHeight: Int,
    private val bannerHeight: Float,
    private val gameRepository: GameRepository,
    private val level: Int,
    private val volumeLevel: Float
) : GLSurfaceView.Renderer {

    /**
     * Выделяем массив для хранения объединеной матрицы. Она будет передана в программу шейдера.
     */
    private val mMVPMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)

    private var pointTime = 0
    private var pointTexCoordLoc = 0
    private var pointSamplerLoc = 0
    private var pointPositionHandle = 0
    private var pointMVPMatrixHandle = 0
    private var picScale = 0f

    private var w = 0f
    private var h = 0f
    private var xOts = 0f
    private var yOts = 0f

    private val lineSize = 10f

    private val traces = Vector<Traces>()

    var rect1: RectangleImage? = null
    var rect2: RectangleImage? = null
    var rect3: RectangleImage? = null
    var rect4: RectangleImage? = null
    var rect5: RectangleImage? = null
    var rect6: RectangleImage? = null
    var rect7: RectangleImage? = null

    // Misc
    var lastTime: Long = 0
    var mProgram = 0
    private val differences: Differences? = null

    // Sound
    var sounds: SoundPool? = null
    var sbeep = 0

    // Our matrices
    private val mtrxProjection = FloatArray(16)
    private val mtrxView = FloatArray(16)
    private val mtrxProjectionAndView = FloatArray(16)

    // Our matrices
    private val mtrxProjection2 = FloatArray(16)
    private val mtrxView2 = FloatArray(16)
    private val mtrxProjectionAndView2 = FloatArray(16)

    // Geometric variables
    private lateinit var vertices: FloatArray
    private lateinit var vertices2: FloatArray
    private lateinit var vertices3: FloatArray
    private lateinit var vertices4: FloatArray
    private lateinit var vertices5: FloatArray
    private lateinit var vertices6: FloatArray
    private lateinit var vertices7: FloatArray

    var fboId = 0
    var fboTex = 0
    var fboId2 = 0
    var fboTex2 = 0

    var picW = 0f
    var picH = 0f

    // Hint
    private var showHiddenHint = false
    private var hiddenHint: HiddenHint? = null
    private var isHiddenHintAnimShowing = false
    private val hintSize = 0f
    private var hintY = 0f
    private var hintX = 0f

    // Bitmap
    private lateinit var dimensions: BitmapFactory.Options
    private lateinit var bitmapMain: Bitmap
    private lateinit var bitmapDifferent: Bitmap
    private lateinit var bitmap: Bitmap
    private var id: Int = 0

    private lateinit var plusOne: GLAnimatedObject

    var particlesTexId = 0

    override fun onDrawFrame(gl: GL10?) {
        val now = System.currentTimeMillis()
        if (lastTime > now) return
        val elapsed: Long = now - lastTime

        differences!!.update_anim(elapsed.toFloat())
        for (i in traces.indices) {
            val destroy: Boolean = traces.elementAt(i).timeAdd(elapsed.toFloat())
            if (destroy) traces.removeAt(i)
        }

        plusOne.update(elapsed)

        if (showHiddenHint) {
            if (isHiddenHintAnimShowing) {
                if (hiddenHint!!.timeAdd(elapsed.toFloat())) {
                    showHiddenHint = false
                    plusOne.start()
                }
            }
        }
        render(mtrxProjectionAndView)
        lastTime = now
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // We need to know the current width and height.
        // We need to know the current width and height.
        screenWidth = width
        screenHeight = height
        // Redo the Viewport, making it fullscreen.
        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, screenWidth, screenHeight)

        // Clear our matrices
        // Clear our matrices
        for (i in 0..15) {
            mtrxProjection[i] = 0.0f
            mtrxView[i] = 0.0f
            mtrxProjectionAndView[i] = 0.0f
        }

        // Setup our screen width and height for normal sprite translation.
        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(
            mtrxProjection,
            0,
            0f,
            screenWidth.toFloat(),
            0.0f,
            screenHeight.toFloat(),
            0f,
            50f
        )

        // Set the camera position (View matrix)
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0)
    }

    private fun setupViewGLES20() {
        // Set the clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        // For transparency PNG
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    private fun createShadersPoint() {
        // Create the shaders, images
        val vertexShader2 =
            riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Point)
        val fragmentShader2 =
            riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Point)

        riGraphicTools.sp_Point = GLES20.glCreateProgram() // create empty OpenGL ES Program
        GLES20.glAttachShader(
            riGraphicTools.sp_Point,
            vertexShader2
        ) // add the vertex shader to program
        GLES20.glAttachShader(
            riGraphicTools.sp_Point,
            fragmentShader2
        ) // add the fragment shader to program

        GLES20.glLinkProgram(riGraphicTools.sp_Point) // creates OpenGL ES program executables
        GLES20.glUseProgram(riGraphicTools.sp_Point)
    }

    private fun initPoint() {
        pointTime = GLES20.glGetUniformLocation(riGraphicTools.sp_Point, "time")
        pointTexCoordLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Point, "a_texCoord")
        pointSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Point, "s_texture")
        pointPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Point, "vPosition")
        pointMVPMatrixHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Point, "uMVPMatrix")
    }

    private fun createShadersImages() {
        // Create the shaders, images
        val vertexShader =
            riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image)
        val fragmentShader =
            riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image)

        riGraphicTools.sp_Image = GLES20.glCreateProgram() // create empty OpenGL ES Program

        GLES20.glAttachShader(
            riGraphicTools.sp_Image,
            vertexShader
        ) // add the vertex shader to program

        GLES20.glAttachShader(
            riGraphicTools.sp_Image,
            fragmentShader
        ) // add the fragment shader to program

        GLES20.glLinkProgram(riGraphicTools.sp_Image) // creates OpenGL ES program executables

        // Set our shader program
        GLES20.glUseProgram(riGraphicTools.sp_Image)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        setupViewGLES20()
        createShadersPoint()
        initPoint()
        createShadersImages()

        dimensions = BitmapFactory.Options()
        dimensions.inScaled = false

        viewModelScope.launch {
            renderingMainImage()
        }


        var temp = IntArray(1)
        //generate fbo id
        GLES20.glGenFramebuffers(1, temp, 0)
        fboId = temp[0]

        //generate texture
        GLES20.glGenTextures(1, temp, 0)
        fboTex = temp[0]

        //generate render buffer
        GLES20.glGenRenderbuffers(1, temp, 0)
        val renderBufferId = temp[0]
        //Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId)
        //Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex)

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
            GLES20.GL_LINEAR)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR)

        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferId)
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
            fboTex,
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
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

        renderingDifferentImage()

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        temp = IntArray(1)
        //generate fbo id
        GLES20.glGenFramebuffers(1, temp, 0)
        fboId2 = temp[0]

        //generate texture
        GLES20.glGenTextures(1, temp, 0)
        fboTex2 = temp[0]

        //generate render buffer
        GLES20.glGenRenderbuffers(1, temp, 0)
        val renderBufferId2 = temp[0]
        //Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId2)
        //Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex2)

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
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferId2)
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
            fboTex2,
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
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

        vertices4 = floatArrayOf(
            0f, 0f, 0.0f,
            0f, picH, 0.0f,
            picW, picH, 0.0f,
            picW, 0f, 0.0f
        )

        rect4 = RectangleImage(vertices4, bitmapMain, 4)

        vertices5 = floatArrayOf(
            0f, 0f, 0.0f,
            0f, picH, 0.0f,
            picW, picH, 0.0f,
            picW, 0f, 0.0f
        )
        rect5 = RectangleImage(vertices5, bitmapDifferent, 5)
        bitmapMain.recycle()
        bitmapDifferent.recycle()

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        vertices3 = floatArrayOf(
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f
        )
        id =
            context.resources.getIdentifier("raw/circle", null, context.packageName)
        bitmap = BitmapFactory.decodeResource(context.resources, id)
        rect3 = RectangleImage(vertices3, bitmap, 2)
        bitmap.recycle()

        //----------------------------------particlesImage
        id = context.resources.getIdentifier("raw/particles", null, context.packageName)
        bitmap = BitmapFactory.decodeResource(context.resources, id, dimensions)

        // Generate Textures, if more needed, alter these numbers.
        val textureNames = IntArray(1)
        GLES20.glGenTextures(1, textureNames, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE6)
        particlesTexId = textureNames[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, particlesTexId)

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()

        renderingHiddenHint()

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, picW.toInt(), picH.toInt())

        // Clear our matrices
        for (i in 0..15) {
            mtrxProjection2[i] = 0.0f
            mtrxView2[i] = 0.0f
            mtrxProjectionAndView2[i] = 0.0f
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection2, 0, 0f, picW, 0.0f, picH, 0f, 50f)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView2, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(
            mtrxProjectionAndView2,
            0,
            mtrxProjection2,
            0,
            mtrxView2,
            0
        )
    }

    private suspend fun renderingMainImage() {
        val game = gameRepository.findGame("")
        bitmapMain = BitmapFactory.decodeFile(game.pathToMainFile)

        picW = bitmapMain.width.toFloat()
        picH = bitmapMain.height.toFloat()

        val picAspectRatio = (picW / picH)

        if (screenHeight < screenWidth) {
            val h1: Float = (screenHeight - bannerHeight).toFloat()
            val w1 = h1 * picAspectRatio
            if (w1 > (screenWidth - lineSize) / 2) {
                w = (screenWidth - lineSize) / 2
                h = w / picAspectRatio
                xOts = 0f
                //--
                yOts = (screenHeight - bannerHeight - h) / 2
                //--
            } else {
                w = w1
                h = h1
                xOts = (screenWidth - lineSize) / 2 - w
                yOts = 0f
            }
        } else {
            val h1: Float = (screenHeight - 2 * bannerHeight - lineSize) / 2
            val w1 = h1 * picAspectRatio
            if (w1 > screenWidth) {
                w = screenWidth.toFloat()
                h = w / picAspectRatio
                yOts = (screenHeight - 2 * bannerHeight - lineSize) / 2 - h
                xOts = 0f
            } else {
                w = w1
                h = h1
                xOts = (screenWidth - w) / 2
                yOts = 0f
            }
        }

        picScale = h / picH

        if (screenHeight < screenWidth) {
            vertices = floatArrayOf(
                xOts, yOts + h, 0.0f,
                xOts, yOts, 0.0f,
                xOts + w, yOts, 0.0f,
                xOts + w, yOts + h, 0.0f
            )
        } else {
            vertices = floatArrayOf(
                xOts, yOts + h, 0.0f,
                xOts, yOts, 0.0f,
                xOts + w, yOts, 0.0f,
                xOts + w, yOts + h, 0.0f
            )
        }

        rect1 = RectangleImage(vertices, bitmapMain, 0)
    }

    private fun renderingDifferentImage() {
        if (screenHeight < screenWidth) {
            vertices2 = floatArrayOf(
                screenWidth - xOts - w, yOts + h, 0.0f,
                screenWidth - xOts - w, yOts, 0.0f,
                screenWidth - xOts, yOts, 0.0f,
                screenWidth - xOts, yOts + h, 0.0f
            )
        } else {
            vertices2 = floatArrayOf(
                xOts, screenHeight - 2 * bannerHeight - yOts, 0.0f,
                xOts, screenHeight - 2 * bannerHeight - h - yOts, 0.0f,
                xOts + w, screenHeight - 2 * bannerHeight - h - yOts, 0.0f,
                xOts + w, screenHeight - 2 * bannerHeight - yOts, 0.0f
            )
        }


        viewModelScope.launch {
            val game = gameRepository.findGame("2")
            bitmapDifferent = BitmapFactory.decodeFile(game.pathToMainFile)
            rect2 = RectangleImage(vertices2, bitmapDifferent, 1)
        }
    }

    private fun renderingHiddenHint() {
        vertices6 = floatArrayOf(
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f,
            -1f, 1f, 0.0f
        )
        vertices7 = floatArrayOf(
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f
        )
        id = context.resources
            .getIdentifier("raw/hidden_hint", null, context.packageName)
        bitmap = BitmapFactory.decodeResource(context.resources, id)
        rect6 = RectangleImage(vertices6, bitmap, 7)
        bitmap.recycle()
        id = context.resources.getIdentifier("raw/plus_one", null, context.packageName)
        bitmap = BitmapFactory.decodeResource(context.resources, id)
        rect7 = RectangleImage(vertices7, bitmap, 8)
        bitmap.recycle()

        plusOne =
            GLAnimatedObject(screenWidth - 1.5f * bannerHeight, 0.0f, rect7, bannerHeight / 2)
        plusOne.moveWithShade(screenWidth - 1.5f * bannerHeight, 1.5f * bannerHeight, 1500L)
    }

    private fun render(m: FloatArray) {
        drawInTexture(fboId, rect4!!)
        drawInTexture(fboId2, rect5!!)
        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, screenWidth, screenHeight)
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // clear Screen and Depth Buffer, we have set the clear color as black.
        GLES20.glClearColor(.0f, .0f, .0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        //GLES20.glReadPixels(x, y, width, height, format, type, pixels)
        GLES20.glUseProgram(riGraphicTools.sp_Image)
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0)
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex)
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        rect1!!.draw(mMVPMatrix)
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex2)
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        rect2!!.draw(mMVPMatrix)
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
//----Draw Hidden Hint Start-------
        if (showHiddenHint) {
            if (isHiddenHintAnimShowing) { //Log.e("Draw Hidden Hint", "Draw Hidden Hint");
//----Draw Hidden Hint traces
                GLES20.glUseProgram(riGraphicTools.sp_Point)
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
                Matrix.setIdentityM(mModelMatrix, 0)
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0)
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0)
                GLES20.glEnableVertexAttribArray(pointTexCoordLoc)
                GLES20.glVertexAttribPointer(
                    pointTexCoordLoc,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    rect6!!.uvBuffer
                )
                GLES20.glUniform1i(pointSamplerLoc, 6)
                GLES20.glEnableVertexAttribArray(pointPositionHandle)
                GLES20.glVertexAttribPointer(
                    pointPositionHandle,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    hiddenHint!!.getvector()
                )
                GLES20.glUniform1f(pointTime, 1500.0f + hiddenHint!!.gettime() / 2.0f)
                GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0)
                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, hiddenHint!!.col())
                GLES20.glDisableVertexAttribArray(pointPositionHandle)
                GLES20.glDisableVertexAttribArray(pointTexCoordLoc)
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                GLES20.glUseProgram(riGraphicTools.sp_Image)
                //----Draw Hidden Hint traces end
                hintX = hiddenHint!!.getX()
                hintY = hiddenHint!!.getY()
                Matrix.setIdentityM(mModelMatrix, 0)
                Matrix.translateM(
                    mModelMatrix,
                    0,
                    hintX,
                    screenHeight - hintY,
                    0.0f
                )
                Matrix.scaleM(
                    mModelMatrix,
                    0,
                    hintSize * hiddenHint!!.scale,
                    hintSize * hiddenHint!!.scale,
                    1f
                )
                Matrix.rotateM(mModelMatrix, 0, 270.0f, 0f, 0f, 1.0f)
                //modelViewMatrix9 = GLKMatrix4Rotate(modelViewMatrix9, 3.14f, 0, 0, 1);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0)
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0)
                rect6!!.draw(mMVPMatrix, 1.0f)
            }
        }
        plusOne.draw(mModelMatrix, mtrxView, mtrxProjection, mMVPMatrix, 1.0f)
        //----Draw Hidden Hint End---------
    }

    private fun drawInTexture(toFboId: Int, rect: RectangleImage) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, toFboId)
        GLES20.glViewport(0, 0, picW.toInt(), picH.toInt())
        GLES20.glClearColor(.0f, .0f, .0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(riGraphicTools.sp_Image)
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0)
        rect.draw(mMVPMatrix, 1.0f)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        //----Draw Hidden Hint Start-------
        if (showHiddenHint) {
            if (!isHiddenHintAnimShowing) { //GLKMatrix4 modelViewMatrix9 = GLKMatrix4Translate(modelViewMatrix, picW - hintX, hintY, 0.0f);
                Matrix.setIdentityM(mModelMatrix, 0)
                Matrix.translateM(mModelMatrix, 0, hintX, hintY, 0.0f)
                Matrix.scaleM(mModelMatrix, 0, hintSize, hintSize, 1f)
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0)
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0)
                rect6!!.draw(mMVPMatrix, 1.0f)
            }
        }
        //----Draw Hidden Hint End---------
        for (i in 0 until differences!!.count) {
            if (differences.finded[i] == 1) {
                Matrix.setIdentityM(mModelMatrix, 0)
                Matrix.translateM(
                    mModelMatrix,
                    0,
                    differences.x[i].toFloat(),
                    differences.y[i].toFloat(),
                    0.0f
                )
                Matrix.scaleM(
                    mModelMatrix,
                    0,
                    differences.r[i].toFloat(),
                    differences.r[i].toFloat(),
                    1f
                )
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0)
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0)
                rect3!!.draw(mMVPMatrix, differences.getAlpha(i))
            }
        }
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glUseProgram(riGraphicTools.sp_Point)
        if (traces.size > 0) {
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
            Matrix.setIdentityM(mModelMatrix, 0)
            Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0)
            Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0)
            var w = 0
            do {
                GLES20.glEnableVertexAttribArray(pointTexCoordLoc)
                GLES20.glVertexAttribPointer(
                    pointTexCoordLoc,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    traces.elementAt(w).uvsBuffer
                )
                GLES20.glUniform1i(pointSamplerLoc, 6)
                GLES20.glEnableVertexAttribArray(pointPositionHandle)
                GLES20.glVertexAttribPointer(
                    pointPositionHandle,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    traces.elementAt(w).vectorBuffer
                )
                GLES20.glUniform1f(pointTime, traces.elementAt(w).gettime())
                GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0)
                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, traces.elementAt(w).col())
                GLES20.glDisableVertexAttribArray(pointPositionHandle)
                GLES20.glDisableVertexAttribArray(pointTexCoordLoc)
                w++
            } while (w < traces.size)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    fun touched(x: Float, y: Float) {
        var y = y
        y = screenHeight - y
        var side = 0
        var xx = -1f
        var yy = -1f
        if (vertices[3] < x && vertices[9] > x && vertices[4] < y && vertices[10] > y
        ) {
            xx = (x - vertices[3]) / picScale
            yy = (y - vertices[4]) / picScale
            side = 1
        }
        if (vertices2[3] < x && vertices2[9] > x && vertices2[4] < y && vertices2[10] > y
        ) {
            xx = (x - vertices2[3]) / picScale
            yy = (y - vertices2[4]) / picScale
            side = 2
        }
        if (xx != -1f && rect1 != null) {
            xx = rect1!!.transX * picW + xx * rect1!!.scale
            yy = rect1!!.transY * picH + (picH - yy) * rect1!!.scale
            val result = differences!!.check(xx.toInt(), yy.toInt())
            if (result != -1) {
                sounds!!.play(sbeep, volumeLevel, volumeLevel, 0, 0, 1.0f)
                gameRepository.setDifferences(result)
                traces.add(
                    Traces(
                        result,
                        differences.getXid(result),
                        differences.getYid(result)
                    )
                ) //добавляем эффект
            } else {
                if (showHiddenHint) {
                    if (hintX - hintSize < xx && xx < hintX + hintSize && hintY - hintSize < yy && yy < hintY + hintSize) {
                        var gx = 0f
                        var gy = 0f
                        if (side == 2) {
                            gx =
                                vertices[3] + (hintX - rect1!!.transX * picW) / rect1!!.scale * picScale
                            gy =
                                (hintY - rect1!!.transY * picH) / rect1!!.scale * picScale + vertices[4]
                            //gy = (vertices[10] - (((picH - hintY - rect1.getTransY()*picH) / rect1.getScale()) * picScale));
                        } else if (side == 1) {
                            gx =
                                vertices2[3] + (hintX - rect1!!.transX * picW) / rect1!!.scale * picScale
                            gy =
                                (hintY - rect1!!.transY * picH) / rect1!!.scale * picScale + vertices2[4] // + banner_height
                        }
                        hiddenHint = HiddenHint(
                            screenWidth - 1.5f * bannerHeight,
                            screenHeight.toFloat(),
                            hintSize,
                            0.toFloat(),
                            gx,
                            gy,
                            picScale / rect1!!.scale,
                            screenHeight.toFloat()
                        )
                        this.hiddenHintFounded()
                    }
                }
            }
        }
    }

    fun hiddenHintFounded() {
        gameRepository.addHint(1)
        gameRepository.setHiddenHintFounded(level)
        isHiddenHintAnimShowing = true
        hiddenHint!!.startAnim()
        sounds!!.play(sbeep, volumeLevel, volumeLevel, 0, 0, 1.0f)
    }

    fun doMove(x: Float, y: Float) {
        if (rect1 != null) {
            rect1!!.translate(x, y)
        }
        if (rect2 != null) {
            rect2!!.translate(x, y)
        }
    }

    fun doScale(x: Float) {
        if (rect1 != null) {
            rect1!!.scale(x)
        }
        if (rect2 != null) {
            rect2!!.scale(x)
        }
    }

    fun useHint() {
        rect1!!.reset()
        rect2!!.reset()
        val id = differences!!.randomDif
        if (id != -1) {
            differences.find(id)
            gameRepository.setDifferences(id)
            gameRepository.subtractOneHint()
            traces.add(
                Traces(
                    id,
                    differences.getXid(id),
                    differences.getYid(id)
                )
            ) //добавляем эффект
            sounds!!.play(sbeep, volumeLevel, volumeLevel, 0, 0, 1.0f)
        }
    }
}