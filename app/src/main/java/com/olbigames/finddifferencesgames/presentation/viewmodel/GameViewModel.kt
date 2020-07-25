package com.olbigames.finddifferencesgames.presentation.viewmodel

import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.olbigames.finddifferencesgames.MainActivity
import com.olbigames.finddifferencesgames.cache.SharedPrefsManager
import com.olbigames.finddifferencesgames.domain.difference.AnimateFoundedDifference
import com.olbigames.finddifferencesgames.domain.difference.DifferenceFounded
import com.olbigames.finddifferencesgames.domain.difference.UpdateDifference
import com.olbigames.finddifferencesgames.domain.game.*
import com.olbigames.finddifferencesgames.domain.hint.HiddenHintFounded
import com.olbigames.finddifferencesgames.renderer.DisplayDimensions
import com.olbigames.finddifferencesgames.renderer.Finger
import com.olbigames.finddifferencesgames.renderer.GameRenderer
import com.olbigames.finddifferencesgames.renderer.helper.DifferencesHelper
import com.olbigames.finddifferencesgames.renderer.helper.GLES20HelperImpl
import com.olbigames.finddifferencesgames.ui.game.GameChangedListener
import com.olbigames.finddifferencesgames.utilities.Constants.DIFFERENCES_NUMBER
import com.olbigames.finddifferencesgames.utilities.Constants.GIFTED_1_HINT
import com.olbigames.finddifferencesgames.utilities.Constants.GIFTED_2_HINTS
import com.olbigames.finddifferencesgames.utilities.Constants.GIFTED_5_HINTS
import com.olbigames.finddifferencesgames.utilities.HandleOnce
import com.olbigames.finddifferencesgames.utilities.getBitmapsForGame
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.sqrt

class GameViewModel @Inject constructor(
    val getGameWithDifferenceUseCase: GetGameWithDifference,
    val foundedCountUseCase: FoundedCount,
    private val sharedPrefsManager: SharedPrefsManager,
    private val updateFoundedCountUseCase: UpdateFoundedCount,
    private val differenceFoundedUseCase: DifferenceFounded,
    private val updateDifferenceUseCase: UpdateDifference,
    private val animateFoundedDifferenceUseCase: AnimateFoundedDifference,
    private val gameCompletedUseCase: GameCompleted,
    private val hiddenHintFoundedUseCase: HiddenHintFounded
) : BaseViewModel(),
    GameChangedListener {

    companion object {
        const val TAG_TOUCH = "Touch handle"
        const val NO_MORE_HINT = 0
        const val BASE_DIALOG_DELAY = 1000L
        const val INCREASED_DIALOG_DELAY = 2000L
        const val GESTURE_TIP_DELAY = 3000L
    }

    private var gameRenderer: GameRenderer? = null
    private lateinit var displayDimensions: DisplayDimensions
    private val fingers: ArrayList<Finger> = ArrayList()

    private var level: Int = 0
    private val showEndLevel = 0
    private var touchLastTime: Long = 0
    private var newTouch = 1
    private var gamesQuantity = 0
    private var difCount = 0
    private var hintNumber = 0
    private var completedDialogShown = false
    private var delayBeforeDialogShow = BASE_DIALOG_DELAY
    private var foundedDifferencesNumber: Int = 0
    private var dialogShowing = false

    private val _gameRendererCreated = MutableLiveData<HandleOnce<GameRenderer>>()
    val gameRendererCreated: LiveData<HandleOnce<GameRenderer>> = _gameRendererCreated

    private val _surfaceCleared = MutableLiveData<Boolean>()
    val surfaceCleared = _surfaceCleared

    private val _needMoreLevelNotify = MutableLiveData<HandleOnce<Boolean>>()
    val needMoreLevelNotify = _needMoreLevelNotify

    private val _foundedCount = MutableLiveData<Int>()
    val foundedCount = _foundedCount

    private val _gameCompletedNotify = MutableLiveData<HandleOnce<Boolean>>()
    val gameCompletedNotify = _gameCompletedNotify

    private val _hintCount = MutableLiveData<Int>()
    val hintCount = _hintCount

    private val _noMoreHint = MutableLiveData<HandleOnce<Boolean>>()
    val noMoreHint = _noMoreHint

    private val _needUseSoundEffect = MutableLiveData<HandleOnce<Boolean>>()
    val needUseSoundEffect = _needUseSoundEffect

    private val _soundEffect = MutableLiveData<HandleOnce<Float>>()
    val soundEffect = _soundEffect

    private val _gestureTipShown = MutableLiveData<HandleOnce<Boolean>>()
    val gestureTipShown = _gestureTipShown

    private val _interstitialAdShown = MutableLiveData<HandleOnce<Boolean>>()
    val interstitialAdShown = _interstitialAdShown

    private val _rateAppShown = MutableLiveData<HandleOnce<Boolean>>()
    val rateAppShown = _rateAppShown

    init {
        level = sharedPrefsManager.getGameLevel()
        hintNumber = sharedPrefsManager.getHiddenHintCount()
        _hintCount.value = hintNumber
        _soundEffect.value = HandleOnce(sharedPrefsManager.getSoundEffect())
        _noMoreHint.value = HandleOnce(sharedPrefsManager.ifNoMoreHint())
        foundedDifferencesNumber = sharedPrefsManager.getFoundedDifferencesNumber()
    }

    private fun handleFoundedCount(foundedCount: Int) {
        difCount = foundedCount
        _foundedCount.value = foundedCount
        determineWhichDialogsToShow()
        if (ifNeedToShowGameCompletedDialog() && !dialogShowing) gameCompleted()
    }

    private fun handleGameWithDifference(gameWithDifferences: GameWithDifferences) {
        completedDialogShown = gameWithDifferences.gameEntity.gameCompleted
        getFoundedCount()
        createGameRenderer(gameWithDifferences)
    }

    fun ifNeedShownGestureTip() {
        if (!sharedPrefsManager.isGestureTipShown()) {
            sharedPrefsManager.gestureTipIsShown(true)
            _gestureTipShown.value = HandleOnce(true)
            GlobalScope.launch {
                delay(GESTURE_TIP_DELAY)
                _gestureTipShown.postValue(HandleOnce(false))
            }
        }
    }

    private fun createGameRenderer(gameWithDifferences: GameWithDifferences) {
        val bitmapList = getBitmapsForGame(
            level,
            gameWithDifferences.gameEntity.pathToMainFile,
            gameWithDifferences.gameEntity.pathToDifferentFile
        )

        gameRenderer = GameRenderer(
            MainActivity.getContext(),
            displayDimensions,
            GLES20HelperImpl(),
            bitmapList[0],
            bitmapList[1],
            DifferencesHelper(),
            this@GameViewModel,
            differenceFoundedUseCase,
            hiddenHintFoundedUseCase,
            updateFoundedCountUseCase,
            animateFoundedDifferenceUseCase,
            updateDifferenceUseCase,
            gameWithDifferences.differences,
            gameWithDifferences.hiddenHint
        )

        gameRenderer?.let {
            _gameRendererCreated.value =
                HandleOnce(it)
        }
    }

    fun startGame() {
        fingers.clear()
        getGameWithDifference()
    }

    fun startNextGame() {
        gamesQuantity = sharedPrefsManager.getGamesQuantity()
        if (level == gamesQuantity) _needMoreLevelNotify.value = HandleOnce(true)
        else initNewGame()
    }

    fun useHint() {
        hintNumber = sharedPrefsManager.getHiddenHintCount()
        if (difCount == 9) delayBeforeDialogShow = INCREASED_DIALOG_DELAY
        if (hintNumber > NO_MORE_HINT && difCount < DIFFERENCES_NUMBER) gameRenderer?.useHint()
    }

    private fun initNewGame() {
        level++
        sharedPrefsManager.saveGameLevel(level)
        _surfaceCleared.value = true
        startGame()
    }

    private fun gameCompleted() {
        hintNumber += GIFTED_2_HINTS
        sharedPrefsManager.saveHiddenHintCount(hintNumber)
        gameCompletedUseCase(GameCompleted.Params(level, true))
        dialogDisplayDelay()
    }

    fun restoreGameState() {
        dialogShowing = false
        getFoundedCount()
    }

    private fun dialogDisplayDelay() {
        viewModelScope.launch {
            delay(delayBeforeDialogShow)
            notifyToShowDialogs()
        }
    }

    private fun notifyToShowDialogs() {
        checkAvailabilityOfHints()
        notifyGameCompletion()
    }

    private fun notifyGameCompletion() {
        _gameCompletedNotify.value =
            HandleOnce(true)
        _hintCount.value = hintNumber
    }

    private fun determineWhichDialogsToShow() {
        if(ifNeedToShowInterstitialAd()) notifyToShowInterstitialAd()
        if(ifNeedToShowRateApp()) notifyToRateApp()
    }

    private fun ifNeedToShowInterstitialAd(): Boolean {
        foundedDifferencesNumber = sharedPrefsManager.getFoundedDifferencesNumber()
        return foundedDifferencesNumber == sharedPrefsManager.getInterstitialInterval()
    }

    private fun notifyToShowInterstitialAd() {
        sharedPrefsManager.addInterstitialInterval()
        _interstitialAdShown.value = HandleOnce(true)
    }

    private fun ifNeedToShowRateApp(): Boolean {
        return foundedDifferencesNumber == sharedPrefsManager.getRateAppInterval()
    }

   private fun notifyToRateApp() {
       sharedPrefsManager.addRateAppInterval()
       _rateAppShown.value = HandleOnce(true)
       dialogShowing = true
   }

    private fun ifNeedToShowGameCompletedDialog(): Boolean {
        return difCount == DIFFERENCES_NUMBER && !completedDialogShown
    }

    private fun checkAvailabilityOfHints() {
        if (hintNumber != NO_MORE_HINT) hintIsAvailable()
        else noMoreAvailableHint()
    }

    private fun hintIsAvailable() {
        _noMoreHint.postValue(HandleOnce(false))
        sharedPrefsManager.isNoMoreHint(false)
    }

    private fun noMoreAvailableHint() {
        _noMoreHint.postValue(HandleOnce(true))
        sharedPrefsManager.isNoMoreHint(true)
    }

    private fun getFoundedCount() {
        foundedCountUseCase(FoundedCount.Params(level)) {
            it.either(
                ::handleFailure,
                ::handleFoundedCount
            )
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

    fun setDisplayMetrics(displayDimensions: DisplayDimensions) {
        this.displayDimensions = displayDimensions
    }

    fun handleTouch(event: MotionEvent, action: Int, pointerId: Int) {
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touched(event, pointerId)
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            released(event)
        } else if (action == MotionEvent.ACTION_MOVE) {
            moved(event)
        }

        if ((showEndLevel != 1) and (level > 0)) {
            if (fingers.size > 1) doScale()
            else if (fingers.size == 1) doTouch(event)
        }
    }

    private fun touched(event: MotionEvent, pointerId: Int) {
        fingers.add(event.actionIndex, Finger(event.x.toInt(), event.y.toInt()))
    }

    private fun moved(event: MotionEvent) {
        var finsize = 0
        if (fingers.size == 1) finsize = 1
        else if (fingers.size > 1) finsize = 2

        for (n in 0 until finsize) { // Обновляем положение всех пальцев
            if (event.pointerCount > n) {
                fingers[n].setNow(event.getX(n).toInt(), event.getY(n).toInt())
            }
        }
    }

    private fun released(event: MotionEvent) {
        try {
            fingers.remove(fingers[event.actionIndex])
        } catch (e: Exception) {
            Log.d(TAG_TOUCH, e.toString())
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
        val xd: Float = (fingers[0].pointBefore.x - fingers[0].pointNow.x).toFloat()
        val yd: Float = (fingers[0].pointBefore.y - fingers[0].pointNow.y).toFloat()
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

    fun addRewardHints() {
        val currentValue = sharedPrefsManager.getHiddenHintCount()
        val newValue = currentValue + GIFTED_5_HINTS
        sharedPrefsManager.saveHiddenHintCount(newValue)
        _hintCount.value = newValue
        if (newValue != 0) {
            sharedPrefsManager.isNoMoreHint(false)
            _noMoreHint.postValue(HandleOnce(false))
        } else {
            _noMoreHint.postValue(HandleOnce(true))
            sharedPrefsManager.isNoMoreHint(true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        foundedCountUseCase.unsubscribe()
        updateFoundedCountUseCase.unsubscribe()
        differenceFoundedUseCase.unsubscribe()
        getGameWithDifferenceUseCase.unsubscribe()
        updateDifferenceUseCase.unsubscribe()
        animateFoundedDifferenceUseCase.unsubscribe()
        gameCompletedUseCase.unsubscribe()
        hiddenHintFoundedUseCase.unsubscribe()
    }

    override fun updateFoundedCount() {
        getFoundedCount()
        sharedPrefsManager.increaseNumberFoundedDifferences()
    }

    override fun updateHintCount() {
        if (hintNumber > NO_MORE_HINT) {
            hintNumber--
            sharedPrefsManager.saveHiddenHintCount(hintNumber)
            _hintCount.value = hintNumber
           hintIsAvailable()
        }
        if (hintNumber == NO_MORE_HINT) {
            _noMoreHint.value = HandleOnce(true)
            sharedPrefsManager.isNoMoreHint(true)
        }
    }

    override fun hiddenHintFounded() {
        hintNumber += GIFTED_1_HINT
        sharedPrefsManager.saveHiddenHintCount(hintNumber)
        _hintCount.value = hintNumber
    }

    override fun makeSoundEffect() {
        _needUseSoundEffect.value = HandleOnce(true)
    }
}
