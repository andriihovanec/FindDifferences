package com.olbigames.finddifferencesgames.ui.game

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.olbigames.finddifferencesgames.db.AppDatabase
import com.olbigames.finddifferencesgames.db.game.GameEntity
import com.olbigames.finddifferencesgames.db.game.GameWithDifferences
import com.olbigames.finddifferencesgames.game.DisplayDimensions
import com.olbigames.finddifferencesgames.game.Finger
import com.olbigames.finddifferencesgames.game.GameRenderer
import com.olbigames.finddifferencesgames.game.helper.DifferencesHelper
import com.olbigames.finddifferencesgames.game.helper.GLES20HelperImpl
import com.olbigames.finddifferencesgames.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.sqrt


class GameViewModel(application: Application) : AndroidViewModel(application),
    GameViewContract.ViewModel, GameChangedListener {

    private var repo: GameRepository
    private lateinit var bitmapMain: Bitmap
    private lateinit var bitmapDifferent: Bitmap
    private var gameRenderer: GameRenderer? = null
    private lateinit var displayDimensions: DisplayDimensions
    private val fingers: ArrayList<Finger> = ArrayList()
    private var level: Int = 0
    private var surfaceStatus = 0
    private val showEndLevel = 0
    private var touchLastTime: Long = 0
    private var newTouch = 1

    private var _foundedGame: MutableLiveData<GameEntity> = MutableLiveData()
    val foundedGame: LiveData<GameEntity> = _foundedGame

    private val _gameRendererCreated = MutableLiveData<GameRenderer>()
    val gameRendererCreated: LiveData<GameRenderer> = _gameRendererCreated

    private val _surfaceCleared = MutableLiveData<Boolean>()
    val surfaceCleared = _surfaceCleared

    private lateinit var gameWithDifferences: GameWithDifferences

    lateinit var foundedCount: LiveData<Int>

    init {
        val gameDao = AppDatabase.getDatabase(application, viewModelScope).gameDao()
        val differencesDao = AppDatabase.getDatabase(application, viewModelScope).differenceDao()
        val hiddenHintDao = AppDatabase.getDatabase(application, viewModelScope).hiddenHintDao()
        repo = GameRepository(gameDao, differencesDao, hiddenHintDao)
    }

    override fun setGameLevel(gameLevel: Int) {
        level = gameLevel
    }

    override fun setDisplayMetrics(displayDimensions: DisplayDimensions) {
        this.displayDimensions = displayDimensions
    }

    override fun startGame() {
        createGameRenderer()
    }

    override fun startNextGame() {
        var nextGameLevel = level
        nextGameLevel++
        level = nextGameLevel
        surfaceStatus = 0
        _surfaceCleared.value = true
        startGame()
    }

    private fun createGameRenderer() {
        if (surfaceStatus != 1) {
            fingers.clear()
            surfaceStatus = 1

            viewModelScope.launch(Dispatchers.IO) {
                gameWithDifferences = repo.getGameWithDifferences(level)
                foundedCount = repo.foundedCount(level)
                viewModelScope.launch(Dispatchers.Main) {
                    bitmapMain =
                        BitmapFactory.decodeFile(gameWithDifferences.gameEntity.pathToMainFile)
                    bitmapDifferent =
                        BitmapFactory.decodeFile(gameWithDifferences.gameEntity.pathToDifferentFile)

                    gameRenderer = GameRenderer(
                        getApplication(),
                        viewModelScope,
                        displayDimensions,
                        level,
                        1f,
                        GLES20HelperImpl(),
                        bitmapMain,
                        bitmapDifferent,
                        gameWithDifferences,
                        DifferencesHelper(
                            gameWithDifferences.differences,
                            repo,
                            this@GameViewModel
                        ),
                        this@GameViewModel
                    )
                    _gameRendererCreated.value = gameRenderer
                }
            }
        }

    }

    private fun update(difference: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.differenceFounded(true, difference)
            repo.updateFoundedCount(level)
        }
    }

    override fun handleTouch(event: MotionEvent, action: Int, pointerId: Int) {
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touched(event, pointerId)
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            released(event)
        } else if (action == MotionEvent.ACTION_MOVE) {
            moved(event)
        }

        if ((surfaceStatus == 1) and (showEndLevel != 1) and (level > 0)) {
            if (fingers.size > 1) {
                doScale()
            } else if (fingers.size == 1) {
                doTouch(event)
            }
        }
    }

    private fun touched(event: MotionEvent, pointerId: Int) {
        fingers.add(event.actionIndex, Finger(pointerId, event.x.toInt(), event.y.toInt()))
    }

    private fun moved(event: MotionEvent) {
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

    private fun released(event: MotionEvent) {
        try {
            if (fingers[event.actionIndex] != null) {
                fingers.remove(fingers[event.actionIndex]) // Удаляем палец, который был отпущен
            }
        } catch (e: Exception) {
            Log.d("TouchHandle", e.toString())
        }
    }

    private fun doScale() {
        val now: Double =
            checkDistance(fingers[0].pointNow, fingers[1].pointNow)
        val before: Double =
            checkDistance(fingers[0].pointBefore, fingers[1].pointBefore)
        gameRenderer?.doScale((before - now).toFloat())
    }

    private fun doTouch(event: MotionEvent) {
        var xd = 0f
        var yd = 0f
        xd = (fingers[0].pointBefore.x - fingers[0].pointNow.x).toFloat()
        yd = (fingers[0].pointBefore.y - fingers[0].pointNow.y).toFloat()
        gameRenderer?.doMove(xd, yd)

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
            gameRenderer?.touched(x, y)
        }
    }

    private fun checkDistance(
        p1: Point,
        p2: Point
    ): Double { // Функция вычисления расстояния между двумя точками
        return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y).toDouble())
    }

    override fun onCleared() {
        super.onCleared()
        gameRenderer = null
    }

    override fun updateFoundedCount(level: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFoundedCount(level)
        }
    }

    override fun differenceFounded(founded: Boolean, differenceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.differenceFounded(founded, differenceId)
        }
    }

    override fun animateFoundedDifference(anim: Float, differenceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.animateFoundedDifference(1000.0f, differenceId)
        }
    }

    override fun updateGameWithDifferences() {
        val result = viewModelScope.async(Dispatchers.IO) {
            repo.getGameWithDifferences(level)
        }
        viewModelScope.launch(Dispatchers.Main) {
            result.await()
        }

        return
    }
}
