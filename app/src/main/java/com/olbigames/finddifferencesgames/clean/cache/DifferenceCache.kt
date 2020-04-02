package com.olbigames.finddifferencesgames.clean.cache

import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import com.olbigames.finddifferencesgames.db.game.GameWithDifferences

interface DifferenceCache {

    fun insertDifference(difference: DifferenceEntity)

    fun getGameWithDifferences(level: Int): GameWithDifferences
    fun differenceFounded(founded: Boolean, differenceId: Int)
    fun updateDifference(difference: DifferenceEntity)
    fun animateFoundedDifference(anim: Float, differenceId: Int)
}