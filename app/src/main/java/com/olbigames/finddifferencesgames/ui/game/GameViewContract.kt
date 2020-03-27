package com.olbigames.finddifferencesgames.ui.game

import android.view.MotionEvent
import com.olbigames.finddifferencesgames.game.DisplayDimensions

interface GameViewContract {

    interface ViewModel {
        fun setGameLevel(gameLevel: Int)
        fun setDisplayMetrics(displayDimensions: DisplayDimensions)
        fun startGame()
        fun startNextGame()
        fun handleTouch(event: MotionEvent, action: Int, pointerId: Int)
    }
}