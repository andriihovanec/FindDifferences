package com.olbigames.finddifferencesgames.clean.domain.game

import com.olbigames.finddifferencesgames.clean.domain.type.Either
import com.olbigames.finddifferencesgames.clean.domain.type.Failure
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import com.olbigames.finddifferencesgames.db.game.GameEntity
import com.olbigames.finddifferencesgames.db.game.GameWithDifferences

interface GetGameRepository {
    fun getGame(level: Int): Either<Failure, GameEntity>
    fun getGameWithDifferences(level: Int): Either<Failure, GameWithDifferences>

    fun foundedCount(level: Int): Either<Failure, Int>
    fun updateFoundedCount(level: Int): Either<Failure, None>

    fun differenceFounded(founded: Boolean, differenceId: Int): Either<Failure, None>
    fun updateDifference(difference: DifferenceEntity): Either<Failure, None>
    fun animateFoundedDifference(anim: Float, differenceId: Int): Either<Failure, None>
}