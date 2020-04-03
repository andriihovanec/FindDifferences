package com.olbigames.finddifferencesgames.ui.game

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.olbigames.finddifferencesgames.MainActivity
import com.olbigames.finddifferencesgames.clean.domain.game.*
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.presentation.viewmodel.BaseViewModel
import com.olbigames.finddifferencesgames.clean.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.clean.domain.games.GameEntity
import com.olbigames.finddifferencesgames.clean.domain.games.GameWithDifferences
import com.olbigames.finddifferencesgames.game.DisplayDimensions
import com.olbigames.finddifferencesgames.game.Finger
import com.olbigames.finddifferencesgames.game.GameRenderer
import com.olbigames.finddifferencesgames.game.helper.DifferencesHelper
import com.olbigames.finddifferencesgames.game.helper.GLES20HelperImpl
import com.olbigames.finddifferencesgames.ui.game.listeners.GameChangedListener
import com.olbigames.finddifferencesgames.ui.game.listeners.NotifyUpdateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.sqrt


class GameViewModel @Inject constructor(
    val getGameUseCase: GetGame,
    val getGameWithDifferenceUseCase: GetGameWithDifference,
    val foundedCountUseCase: FoundedCount,
    val updateFoundedCountUseCase: UpdateFoundedCount,
    val differenceFoundedUseCase: DifferenceFounded,
    val updateDifferenceUseCase: UpdateDifference,
    val animateFoundedDifferenceUseCase: AnimateFoundedDifference
) : BaseViewModel(),
    GameViewContract.ViewModel,
    GameChangedListener {

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

    private val _differenceFounded = MutableLiveData<None>()
    val differenceFounded = _differenceFounded

    private val _animateFoundedDifference = MutableLiveData<None>()
    val animateFoundedDifference = _animateFoundedDifference

    private val _updateFoundedCount = MutableLiveData<None>()
    val updateFoundedCount = _updateFoundedCount

    private var gameWithDifferences: GameWithDifferences? = null

    private val _foundedCount = MutableLiveData<Int>()
    val foundedCount = _foundedCount

    override fun setGameLevel(gameLevel: Int) {
        level = gameLevel
        getGameUseCase(GetGame.Params(level)) {
            it.either(::handleFailure) { game ->
                handleGetGame(game)
            }
        }
    }

    private fun handleGetGame(game: GameEntity) {
        _foundedGame.value = game
    }

    private fun handleFoundedCount(game: Int) {
        _foundedCount.value = game
    }

    private fun handleGameWithDifference(gameWithDifference: GameWithDifferences) {
        gameWithDifferences = gameWithDifference
        getFoundedCount()
        bitmapMain =
            BitmapFactory.decodeFile(gameWithDifferences!!.gameEntity.pathToMainFile)
        bitmapDifferent =
            BitmapFactory.decodeFile(gameWithDifferences!!.gameEntity.pathToDifferentFile)

        gameRenderer = GameRenderer(
            MainActivity.getContext(),
            viewModelScope,
            displayDimensions,
            level,
            1f,
            GLES20HelperImpl(),
            bitmapMain,
            bitmapDifferent,
            gameWithDifferences!!,
            DifferencesHelper(
                gameWithDifferences!!.differences,
                this@GameViewModel
            ),
            this@GameViewModel
        )
        _gameRendererCreated.value = gameRenderer
    }

    private fun handleDifferenceFounded(none: None) {
        _differenceFounded.value = none
    }

    private fun handleUpdateFoundedCount(none: None) {
        _differenceFounded.value = none
    }

    private fun handleAnimateFoundedDifference(none: None) {
        _animateFoundedDifference.value = none
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
            getGameWithDifference()
        }

    }

    private fun getFoundedCount() {
        foundedCountUseCase(FoundedCount.Params(level)) {
            it.either(
                ::handleFailure,
                ::handleFoundedCount
            )
        }
    }

    private fun update(difference: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            differenceFounded(difference)
            updateFoundedCount()
        }
    }

    private fun getGameWithDifference() {
        getGameWithDifferenceUseCase(GetGameWithDifference.Params(level)) {
            it.either(
                ::handleFailure,
                ::handleGameWithDifference
            )
        }
    }

    private fun differenceFounded(difference: Int) {
        differenceFoundedUseCase(DifferenceFounded.Params(true, difference)) {
            it.either(
                ::handleFailure,
                ::handleDifferenceFounded
            )
        }
    }

    private fun animateFoundedDiff(anim: Float, differenceId: Int) {
        animateFoundedDifferenceUseCase(AnimateFoundedDifference.Params(1000.0f, differenceId))
    }

    private fun updateFoundedCount() {
        updateFoundedCountUseCase(UpdateFoundedCount.Params(level)) {
            it.either(
                ::handleFailure,
                ::handleUpdateFoundedCount
            )
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
        getGameUseCase.unsubscribe()
        foundedCountUseCase.unsubscribe()
    }

    override fun updateFoundedCount(level: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            updateFoundedCount()
        }
    }

    override fun differenceFounded(founded: Boolean, differenceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            differenceFounded(differenceId)
        }
    }

    override fun updateDifference(difference: DifferenceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            //repo.updateDifference(difference)
        }
    }

    override fun animateFoundedDifference(anim: Float, differenceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            animateFoundedDiff(anim, differenceId)
        }
    }

    override fun updateGameWithDifferences(notify: NotifyUpdateListener) {
        surfaceStatus = 0
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getGameWithDifference()
                //val updatedGame = repo.getGameWithDifferences(level)
                //notify.notifyUpdateData(updatedGame)
            } catch (e: Exception) {
                Log.d("Lo", "kkk")
            }

        }

        //startGame()
    }
}
