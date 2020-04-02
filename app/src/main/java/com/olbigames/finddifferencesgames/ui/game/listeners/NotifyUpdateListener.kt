package com.olbigames.finddifferencesgames.ui.game.listeners

import com.olbigames.finddifferencesgames.db.game.GameWithDifferences

interface NotifyUpdateListener {
    fun notifyUpdateData(updatedDame: GameWithDifferences)
}