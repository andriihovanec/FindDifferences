package com.olbigames.finddifferencesgames.ui.game.listeners

import com.olbigames.finddifferencesgames.clean.domain.games.GameWithDifferences

interface NotifyUpdateListener {
    fun notifyUpdateData(updatedDame: GameWithDifferences)
}