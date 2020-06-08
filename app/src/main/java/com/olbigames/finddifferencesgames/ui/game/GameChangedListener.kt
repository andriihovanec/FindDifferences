package com.olbigames.finddifferencesgames.ui.game

interface GameChangedListener {
    fun updateFoundedCount(level: Int)
    fun updateHiddenHintCount(level: Int)
    fun makeSoundEffect()
}