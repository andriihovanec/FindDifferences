package com.olbigames.finddifferencesgames.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import com.olbigames.finddifferencesgames.domain.difference.AnimateFoundedDifference
import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.difference.DifferenceFounded
import com.olbigames.finddifferencesgames.domain.difference.UpdateDifference
import com.olbigames.finddifferencesgames.domain.game.UpdateFoundedCount
import com.olbigames.finddifferencesgames.domain.hint.HiddenHintEntity
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import com.olbigames.finddifferencesgames.renderer.helper.DifferencesHelper
import com.olbigames.finddifferencesgames.renderer.helper.GLES20HelperImpl
import com.olbigames.finddifferencesgames.renderer.helper.HiddenHintHelper
import com.olbigames.finddifferencesgames.renderer.helper.VerticesHelper
import com.olbigames.finddifferencesgames.ui.game.GameChangedListener
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class GameRenderer(
    val context: Context,
    private var displayDimensions: DisplayDimensions,
    private val level: Int,
    private val GLES20Helper: GLES20HelperImpl,
    private val mainBitmap: Bitmap,
    private val differentBitmap: Bitmap,
    private val differencesHelper: DifferencesHelper,
    private val gameChangeListener: GameChangedListener,
    val differenceFoundedUseCase: DifferenceFounded,
    val updateFoundedCountUseCase: UpdateFoundedCount,
    val animateFoundedDifferenceUseCase: AnimateFoundedDifference,
    val updateDifferenceUseCase: UpdateDifference,
    private var differences: List<DifferenceEntity>,
    private var hiddenHint: HiddenHintEntity
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

    private val traces = Vector<Traces>()

    private var rect1: RectangleImage? = null
    private var rect2: RectangleImage? = null
    private var rect3: RectangleImage? = null
    private var rect4: RectangleImage? = null
    private var rect5: RectangleImage? = null
    private var rect6: RectangleImage? = null
    private var rect7: RectangleImage? = null

    // Misc
    private var lastTime: Long = 0

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

    private var picW = 0f
    private var picH = 0f

    // Hint
    private var hiddenHintHelper: HiddenHintHelper? = null
    private var showHiddenHint = false
    //private var hiddenHint: HiddenHint? = null
    private var isHiddenHintAnimShowing = false
    private var hintSize = 0f
    private var hintY = 0f
    private var hintX = 0f

    // Bitmap
    private lateinit var dimensions: BitmapFactory.Options
    private lateinit var bitmap: Bitmap
    private var id: Int = 0

    private lateinit var plusOne: GLAnimatedObject

    private var particlesTexId = 0

    init {
        hintX = hiddenHint.hintCoordinateAxisX.toFloat()
        hintY = hiddenHint.hintCoordinateAxisY.toFloat()
        hintSize = hiddenHint.radius.toFloat()
        lastTime = System.currentTimeMillis() + 100
    }

    private fun handleFailure(failure: Failure) {
        when (failure) {
            Failure.NetworkConnectionError -> Log.d("DbError", "Failure to get game")
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        val now = System.currentTimeMillis()
        if (lastTime > now) return
        val elapsed: Long = now - lastTime

        updateAnim(elapsed.toFloat())

        for (i in 0..traces.size) {
            if (i < traces.size) {
                val destroy: Boolean = traces.elementAt(i).timeAdd(elapsed.toFloat())
                if (destroy) traces.removeAt(i)
            }
        }

        plusOne.update(elapsed)

        showHiddenHint = !hiddenHint.founded

        if (showHiddenHint) {
            if (isHiddenHintAnimShowing) {
                if (hiddenHintHelper!!.timeAdd(elapsed.toFloat())) {
                    showHiddenHint = false
                    plusOne.start()
                }
            }
        }
        render()
        lastTime = now
    }

    private fun updateAnim(time: Float) {
        for (i in 0 until differences.count()) {
            if (differences[i].anim != 0.0f && differences[i].anim > time) {
                differences[i].anim -= time
                updateDifferenceUseCase(UpdateDifference.Params(differences[i]))
            } else {
                differences[i].anim = 0.0f
                updateDifferenceUseCase(UpdateDifference.Params(differences[i]))
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // We need to know the current width and height.
        displayDimensions.displayW = width
        displayDimensions.displayH = height
        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(
            0,
            0,
            displayDimensions.displayW,
            displayDimensions.displayH
        ) // Make Viewport full screen

        // Clear our matrices
        for (i in 0..15) {
            mtrxProjection[i] = 0.0f
            mtrxView[i] = 0.0f
            mtrxProjectionAndView[i] = 0.0f
        }
        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(
            mtrxProjection,
            0,
            0f,
            displayDimensions.displayW.toFloat(),
            0.0f,
            displayDimensions.displayH.toFloat(),
            0f,
            50f
        )
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0)
    }

    private fun initPoint() {
        pointTime = GLES20.glGetUniformLocation(GraphicTools.sp_Point, "time")
        pointTexCoordLoc = GLES20.glGetUniformLocation(GraphicTools.sp_Point, "a_texCoord")
        pointSamplerLoc = GLES20.glGetUniformLocation(GraphicTools.sp_Point, "s_texture")
        pointPositionHandle = GLES20.glGetAttribLocation(GraphicTools.sp_Point, "vPosition")
        pointMVPMatrixHandle = GLES20.glGetUniformLocation(GraphicTools.sp_Point, "uMVPMatrix")
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20Helper.setupViewGLES20()
        GLES20Helper.createShadersPoint()
        initPoint()
        GLES20Helper.createShadersImages()

        dimensions = BitmapFactory.Options()
        dimensions.inScaled = false

        renderingMainImage()
        GLES20Helper.initGLES20MainImage(picW, picH)
        renderingDifferentImage()
        GLES20Helper.initGLES20DifferentImage(picW, picH)

        createRectangleMain()
        createRectangleDifferent()
        createRectangleCircle()
        createRectangleParticles()
        createRectangleHiddenHint()
        createRectanglePlusOne()

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, picW.toInt(), picH.toInt()) // Make Viewport full screen

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

    private fun renderingMainImage() {
        picW = mainBitmap.width.toFloat()
        picH = mainBitmap.height.toFloat()

        picScale =
            VerticesHelper.calculatePicScale(
                displayDimensions.displayH.toFloat(),
                displayDimensions.displayW.toFloat(),
                picW,
                picH
            )
        vertices = VerticesHelper.verticesForMainBitmap()
        rect1 = RectangleImage(vertices, mainBitmap, 0)
    }

    private fun renderingDifferentImage() {
        vertices2 =
            VerticesHelper.verticesForDifferentBitmap(
                displayDimensions.displayH.toFloat(),
                displayDimensions.displayW.toFloat()
            )
        rect2 = RectangleImage(vertices2, differentBitmap, 1)
    }

    private fun createRectangleMain() {
        vertices4 = VerticesHelper.getVertices4(picW, picH)
        rect4 = RectangleImage(vertices4, mainBitmap, 4)
    }

    private fun createRectangleDifferent() {
        vertices5 = VerticesHelper.getVertices5(picW, picH)
        rect5 = RectangleImage(vertices5, differentBitmap, 5)
        mainBitmap.recycle()
        differentBitmap.recycle()
    }

    private fun createRectangleCircle() {
        vertices3 = VerticesHelper.getVertices3()
        id =
            context.resources.getIdentifier("raw/circle", null, context.packageName)
        bitmap = BitmapFactory.decodeResource(context.resources, id)
        rect3 = RectangleImage(vertices3, bitmap, 2)
        bitmap.recycle()
    }

    private fun createRectangleParticles() {
        // Particles Image
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
    }

    private fun createRectanglePlusOne() {
        vertices7 = VerticesHelper.getVertices7()
        id = context.resources.getIdentifier("raw/plus_one", null, context.packageName)
        bitmap = BitmapFactory.decodeResource(context.resources, id)
        rect7 = RectangleImage(vertices7, bitmap, 8)
        bitmap.recycle()
        createPlusOneAnimation()
    }

    private fun createPlusOneAnimation() {
        plusOne =
            GLAnimatedObject(
                displayDimensions.displayW - 1.5f * displayDimensions.bannerHeight,
                0.0f,
                rect7,
                (displayDimensions.bannerHeight / 2).toFloat()
            )
        plusOne.moveWithShade(
            displayDimensions.displayW - 1.5f * displayDimensions.bannerHeight,
            1.5f * displayDimensions.bannerHeight,
            1500L
        )
    }

    private fun createRectangleHiddenHint() {
        vertices6 = VerticesHelper.getVertices6()
        id = context.resources
            .getIdentifier("raw/hidden_hint", null, context.packageName)
        bitmap = BitmapFactory.decodeResource(context.resources, id)
        rect6 = RectangleImage(vertices6, bitmap, 7)
        bitmap.recycle()
    }

    private fun render() {
        drawInTexture(GLES20Helper.fboId, rect4!!)
        drawInTexture(GLES20Helper.fboId2, rect5!!)
        GLES20.glViewport(
            0,
            0,
            displayDimensions.displayW,
            displayDimensions.displayH
        ) // Make Viewport full screen
        GLES20Helper.clearScreenAndDepthBuffer()
        Matrix.setIdentityM(mModelMatrix, 0)
        multiplyMatrices()
        drawBitmaps()
        startDrawHiddenHintTracer()
    }

    private fun drawBitmaps() {
        // Выбираем текущий слот для работы
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0) // GLES20.GL_TEXTUREx – номер выбранного слота
        // glBindTexture используется для подключения текстуры к слоту.
        // Первый параметр – тип текстуры, второй – ссылка на текстуру.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20Helper.fboTexture)
        rect1!!.draw(mMVPMatrix)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20Helper.fboTexture2)
        rect2!!.draw(mMVPMatrix)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    private fun startDrawHiddenHintTracer() {
        if (showHiddenHint) {
            if (isHiddenHintAnimShowing) {
                drawHiddenHintTracer()
                hintX = hiddenHintHelper!!.hintCoordinateAxisX
                hintY = hiddenHintHelper!!.hintCoordinateAxisY

                Matrix.setIdentityM(mModelMatrix, 0)
                Matrix.translateM(
                    mModelMatrix,
                    0,
                    hintX,
                    displayDimensions.displayH - hintY,
                    0.0f
                )
                Matrix.scaleM(
                    mModelMatrix,
                    0,
                    hintSize * hiddenHintHelper!!.getScale(),
                    hintSize * hiddenHintHelper!!.getScale(),
                    1f
                )
                Matrix.rotateM(mModelMatrix, 0, 270.0f, 0f, 0f, 1.0f)
                multiplyMatrices()
                rect6!!.draw(mMVPMatrix, 1.0f)
            }
        }
        plusOne.draw(mModelMatrix, mtrxView, mtrxProjection, mMVPMatrix, 1.0f)
    }

    private fun drawHiddenHintTracer() {
        GLES20.glUseProgram(GraphicTools.sp_Point)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
        Matrix.setIdentityM(mModelMatrix, 0)
        multiplyMatrices()
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
            0
        )
        GLES20.glUniform1f(pointTime, 1500.0f + hiddenHintHelper!!.getTime() / 2.0f)
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        //GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 200)
        disablePointVertexArray()
        GLES20Helper.createTextureTransparency()
        GLES20.glUseProgram(GraphicTools.sp_Image)
    }

    private fun disablePointVertexArray() {
        GLES20.glDisableVertexAttribArray(pointPositionHandle)
        GLES20.glDisableVertexAttribArray(pointTexCoordLoc)
    }

    private fun drawInTexture(toFboId: Int, rect: RectangleImage) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, toFboId)
        GLES20.glViewport(0, 0, picW.toInt(), picH.toInt()) // Make Viewport full screen
        GLES20Helper.clearScreenAndDepthBuffer()
        Matrix.setIdentityM(mModelMatrix, 0)
        multiplyMatrices2()
        rect.draw(mMVPMatrix, 1.0f)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        drawHiddenHint()
        startDrawDifference()
        GLES20Helper.createTextureTransparency()
        startDifferenceExplosionAnimation()
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    private fun startDifferenceExplosionAnimation() {
        GLES20.glUseProgram(GraphicTools.sp_Point)
        if (traces.size > 0) {
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
            Matrix.setIdentityM(mModelMatrix, 0)
            multiplyMatrices2()
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
                disablePointVertexArray()
                w++
            } while (w < traces.size)
            GLES20Helper.createTextureTransparency()
        }
    }

    private fun drawHiddenHint() {
        if (showHiddenHint) {
            if (!isHiddenHintAnimShowing) {
                Matrix.setIdentityM(mModelMatrix, 0)
                Matrix.translateM(mModelMatrix, 0, hintX, hintY, 0.0f)
                Matrix.scaleM(mModelMatrix, 0, hintSize, hintSize, 1f)
                multiplyMatrices2()
                rect6!!.draw(mMVPMatrix, 1.0f)
            }
        }
    }

    private fun startDrawDifference() {
        for (i in 0 until differences.count()) {
            if (differences[i].founded) {
                Matrix.setIdentityM(mModelMatrix, 0)
                Matrix.translateM(
                    mModelMatrix,
                    0,
                    differences[i].x.toFloat(),
                    differences[i].y.toFloat(),
                    0.0f
                )
                Matrix.scaleM(
                    mModelMatrix,
                    0,
                    differences[i].r.toFloat(),
                    differences[i].r.toFloat(),
                    1f
                )
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0)
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0)
                rect3!!.draw(mMVPMatrix, differencesHelper.getAlpha(differences, i))
            }
        }
    }

    private fun multiplyMatrices() {
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0)
    }

    private fun multiplyMatrices2() {
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0)
    }

    fun touched(eventX: Float, eventY: Float) {
        var y = eventY
        y = displayDimensions.displayH - y
        var side = 0
        var xx = -1f
        var yy = -1f
        if (vertices[3] < eventX && vertices[9] > eventX && vertices[4] < y && vertices[10] > y
        ) {
            xx = (eventX - vertices[3]) / picScale
            yy = (y - vertices[4]) / picScale
            side = 1
        }
        if (vertices2[3] < eventX && vertices2[9] > eventX && vertices2[4] < y && vertices2[10] > y
        ) {
            xx = (eventX - vertices2[3]) / picScale
            yy = (y - vertices2[4]) / picScale
            side = 2
        }
        if (xx != -1f && rect1 != null) {
            rect1?.let {
                xx = it.transX * picW + xx * it.scale
                yy = it.transY * picH + (picH - yy) * it.scale
            }

            val differenceResult =
                differencesHelper.checkDifference(differences, xx.toInt(), yy.toInt())
            if (differenceResult != -1) {
                differenceFounded(differenceResult)
            } else {
                if (showHiddenHint) {
                    showHiddenHint(xx, yy, side)
                }
            }
        }
    }

    private fun differenceFounded(differenceResult: Int) {
        updateFoundedDifference(differenceResult - 1)
        traces.add(
            Traces(
                differenceResult,
                differencesHelper.getXid(differences, differenceResult),
                differencesHelper.getYid(differences, differenceResult)
            )
        ) //добавляем эффект
    }

    private fun updateFoundedDifference(id: Int) {
        differences[id].founded = true
        differenceFoundedUseCase(DifferenceFounded.Params(true, differences[id].differenceId))
        updateFoundedCountUseCase(UpdateFoundedCount.Params(differences[id].levelId)) {
            it.either(
                ::handleFailure,
                ::handleUpdateFoundedCount
            )
        }
        differences[id].anim = 1000.0f
        animateFoundedDifferenceUseCase(
            AnimateFoundedDifference.Params(
                1000.0f,
                differences[id].differenceId
            )
        )
    }

    private fun handleUpdateFoundedCount(none: None) {
        gameChangeListener.updateFoundedCount()
        gameChangeListener.makeSoundEffect()
    }

    private fun showHiddenHint(xx: Float, yy: Float, side: Int) {
        if (hintX - hintSize < xx && xx < hintX + hintSize && hintY - hintSize < yy && yy < hintY + hintSize) {
            var gx = 0f
            var gy = 0f
            if (side == 2) {
                gx =
                    vertices[3] + (hintX - rect1!!.transX * picW) / rect1!!.scale * picScale
                gy =
                    (hintY - rect1!!.transY * picH) / rect1!!.scale * picScale + vertices[4]
            } else if (side == 1) {
                gx =
                    vertices2[3] + (hintX - rect1!!.transX * picW) / rect1!!.scale * picScale
                gy =
                    (hintY - rect1!!.transY * picH) / rect1!!.scale * picScale + vertices2[4]
            }
            hiddenHintHelper = HiddenHintHelper(
                displayDimensions.displayW - 1.5f * displayDimensions.bannerHeight,
                displayDimensions.displayH.toFloat(),
                false,
                gx,
                gy,
                picScale / rect1!!.scale
            )
            hiddenHintFounded()
        }
    }

    private fun hiddenHintFounded() {
        isHiddenHintAnimShowing = true
        hiddenHintHelper!!.startAnim()
        gameChangeListener.makeSoundEffect()
    }

    fun doMove(x: Float, y: Float) {
        rect1?.translate(x, y)
        rect2?.translate(x, y)
    }

    fun doScale(x: Float) {
        rect1?.scale(x)
        rect2?.scale(x)
    }

    fun useHint() {
        rect1?.reset()
        rect2?.reset()
        val id = differencesHelper.getRandomDif(differences)
        if (id != -1) {
            updateFoundedDifference(id - 1)
            gameChangeListener.updateHiddenHintCount()
            traces.add(
                Traces(
                    id,
                    differencesHelper.getXid(differences, id),
                    differencesHelper.getYid(differences, id)
                )
            ) //добавляем эффект
        }
    }
}