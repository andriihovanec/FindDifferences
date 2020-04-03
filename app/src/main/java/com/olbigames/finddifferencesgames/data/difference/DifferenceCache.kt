package com.olbigames.finddifferencesgames.data.difference

import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.game.GameWithDifferences

interface DifferenceCache {

    fun insertDifference(difference: DifferenceEntity)

    fun getGameWithDifferences(level: Int): GameWithDifferences
    fun differenceFounded(founded: Boolean, differenceId: Int)
    fun updateDifference(difference: DifferenceEntity)
    fun animateFoundedDifference(anim: Float, differenceId: Int)
}