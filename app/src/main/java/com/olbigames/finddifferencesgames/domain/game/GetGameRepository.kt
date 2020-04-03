package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.games.GameEntity
import com.olbigames.finddifferencesgames.domain.games.GameWithDifferences
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None

interface GetGameRepository {
    fun getGame(level: Int): Either<Failure, GameEntity>
    fun getGameWithDifferences(level: Int): Either<Failure, GameWithDifferences>

    fun foundedCount(level: Int): Either<Failure, Int>
    fun updateFoundedCount(level: Int): Either<Failure, None>

    fun differenceFounded(founded: Boolean, differenceId: Int): Either<Failure, None>
    fun updateDifference(difference: DifferenceEntity): Either<Failure, None>
    fun animateFoundedDifference(anim: Float, differenceId: Int): Either<Failure, None>
}