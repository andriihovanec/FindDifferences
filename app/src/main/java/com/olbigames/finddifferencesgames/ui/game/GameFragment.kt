package com.olbigames.finddifferencesgames.ui.game

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.olbigames.finddifferencesgames.Constants.GAME_LEVEL_KEY
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.db.AppDatabase
import com.olbigames.finddifferencesgames.extension.checkIsSupportsEs2
import com.olbigames.finddifferencesgames.game.Finger
import com.olbigames.finddifferencesgames.game.GameRenderer
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*
import kotlin.math.sqrt


class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var viewModel: GameViewModel
    private val fingers: ArrayList<Finger> = ArrayList<Finger>()
    private lateinit var gameRenderer: GameRenderer
    private var gameLevel: Int = 0
    private val surfaceStatus = 0
    private val showEndLevel = 0
    private var touchLastTime: Long = 0
    private var newTouch = 1

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        getGameLevel()
        setupGame()
        startGameRender()
        handleClick()
        handleTouch()
    }

    private fun getGameLevel() {
        arguments.let { bundle ->
            bundle?.getString(GAME_LEVEL_KEY)?.let { level ->
                gameLevel = level.toInt()
            }
        }
    }

    private fun setupGame() {
        viewModel.foundedGame.observe(this, Observer { game ->
            image_top_imageview.setImageURI(Uri.parse(game.pathToDifferentFile))
            image_bottom_imageview.setImageURI(Uri.parse(game.pathToMainFile))
        })
    }

    private fun handleClick() {
        all_game.setOnClickListener { findNavController().navigateUp() }
        next_game.setOnClickListener { viewModel.startNextGame() }
    }

    private fun handleTouch() {
        gameSurface.setOnTouchListener { v, event ->
            val id = event.getPointerId(event.actionIndex)
            val action = event.actionMasked
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                Toast.makeText(requireContext(), "$id X-${event.x} Y-${event.y}", Toast.LENGTH_SHORT).show()
                fingers.add(event.actionIndex, Finger(id, event.x.toInt(), event.y.toInt()))
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                try {
                    if (fingers[event.actionIndex] != null) {
                        fingers.remove(fingers[event.actionIndex]) // Удаляем палец, который был отпущен
                    }
                } catch (e2: Exception) {
                }
            } else if (action == MotionEvent.ACTION_MOVE) {
                var finsize = 0
                if (fingers.size == 1) {
                    finsize = 1
                } else if (fingers.size > 1) {
                    finsize = 2
                }
                for (n in 0 until finsize) { // Обновляем положение всех пальцев
                    if (event.pointerCount > n) {
                        fingers[n].setNow(event.getX(n).toInt(), event.getY(n).toInt())
                    }
                }
            }

            if ((surfaceStatus == 1) and (showEndLevel != 1) and (gameLevel > 0)) {
                if (fingers.size > 1) {
                    val now: Double =
                        checkDistance(fingers[0].Now, fingers[1].Now)
                    val before: Double =
                        checkDistance(fingers[0].Before, fingers[1].Before)
                    if (gameRenderer != null) {
                        gameRenderer.doScale((before - now).toFloat())
                    }
                } else if (fingers.size == 1) { //if(fingers.size() == 1){
                    var xd = 0f
                    var yd = 0f
                    xd = (fingers[0].Before.x - fingers[0].Now.x).toFloat()
                    yd = (fingers[0].Before.y - fingers[0].Now.y).toFloat()
                    if (gameRenderer != null) {
                        gameRenderer.doMove(xd, yd)
                    }
                    //}
//----------------------------------------
                    val now = System.currentTimeMillis()
                    val elapsed: Long = now - touchLastTime
                    if (elapsed > 100 && newTouch != 1) {
                        newTouch = 1
                    } else {
                        newTouch = 0
                        touchLastTime = now
                    }
                    if (newTouch == 1) {
                        val x: Float = event.x
                        val y: Float = event.y
                        gameRenderer.touched(x, y)
                    }
                    //----------------------------------------
                }
            }
            true
        }
    }

    private fun checkDistance(
        p1: Point,
        p2: Point
    ): Double { // Функция вычисления расстояния между двумя точками
        return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y).toDouble())
    }

    private fun startGameRender() {
        if (activity!!.checkIsSupportsEs2()) {
            gameSurface.setEGLContextClientVersion(2)
            gameSurface.setEGLConfigChooser(8, 8, 8, 8, 16, 0)

            val metrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metrics)
            val displayW = metrics.widthPixels
            val displayH = metrics.heightPixels
            val bannerHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50f,
                resources.displayMetrics
            ).toInt()

            viewModel.startGameRenderer(displayW, displayH, bannerHeight, gameLevel)
            viewModel.gameRender.observe(this, Observer {
                gameSurface.setRenderer(it)
            })

        }
    }
}
