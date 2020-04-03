package com.olbigames.finddifferencesgames.cache.difference

import com.olbigames.finddifferencesgames.clean.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.clean.domain.games.GameWithDifferences

interface DifferenceCache {

    fun insertDifference(difference: DifferenceEntity)

    fun getGameWithDifferences(level: Int): GameWithDifferences
    fun differenceFounded(founded: Boolean, differenceId: Int)
    fun updateDifference(difference: DifferenceEntity)
    fun animateFoundedDifference(anim: Float, differenceId: Int)
}