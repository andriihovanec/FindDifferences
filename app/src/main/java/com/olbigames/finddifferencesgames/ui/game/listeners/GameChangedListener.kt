package com.olbigames.finddifferencesgames.ui.game.listeners

import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity

interface GameChangedListener {
    fun updateFoundedCount(level: Int)
    fun differenceFounded(founded: Boolean, differenceId: Int)
    fun updateDifference(difference: DifferenceEntity)
    fun animateFoundedDifference(anim: Float, differenceId: Int)
    fun updateGameWithDifferences(notify: NotifyUpdateListener)
}