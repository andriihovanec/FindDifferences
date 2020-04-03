package com.olbigames.finddifferencesgames.ui.game.listeners

import com.olbigames.finddifferencesgames.domain.game.GameWithDifferences

interface NotifyUpdateListener {
    fun notifyUpdateData(updatedDame: GameWithDifferences)
}