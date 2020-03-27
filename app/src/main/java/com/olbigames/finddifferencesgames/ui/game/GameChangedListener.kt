package com.olbigames.finddifferencesgames.ui.game

interface GameChangedListener {
    fun updateFoundedCount(level: Int)
    fun differenceFounded(founded: Boolean, differenceId: Int)
    fun animateFoundedDifference(anim: Float, differenceId: Int)
    fun updateGameWithDifferences()
}