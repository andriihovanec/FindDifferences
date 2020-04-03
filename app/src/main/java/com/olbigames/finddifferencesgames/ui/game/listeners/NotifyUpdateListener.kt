package com.olbigames.finddifferencesgames.ui.game.listeners

import com.olbigames.finddifferencesgames.domain.games.GameWithDifferences

interface NotifyUpdateListener {
    fun notifyUpdateData(updatedDame: GameWithDifferences)
}