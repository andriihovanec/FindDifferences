package com.olbigames.finddifferencesgames.ui.game

interface GameChangedListener {
    fun updateFoundedCount()
    fun updateHintCount()
    fun hiddenHintFounded()
    fun makeSoundEffect()
}