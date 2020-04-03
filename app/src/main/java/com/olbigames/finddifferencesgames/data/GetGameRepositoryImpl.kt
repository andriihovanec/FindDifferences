package com.olbigames.finddifferencesgames.data

import com.olbigames.finddifferencesgames.cache.difference.DifferenceCache
import com.olbigames.finddifferencesgames.cache.game.GameCache
import com.olbigames.finddifferencesgames.clean.domain.game.GetGameRepository
import com.olbigames.finddifferencesgames.clean.domain.type.Either
import com.olbigames.finddifferencesgames.clean.domain.type.Failure
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.clean.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.clean.domain.games.GameEntity
import com.olbigames.finddifferencesgames.clean.domain.games.GameWithDifferences

class GetGameRepositoryImpl(
    private val gameCache: GameCache,
    private val differenceCache: DifferenceCache
) : GetGameRepository {

    override fun getGame(level: Int): Either<Failure, GameEntity> {
        return Either.Right(gameCache.getGame(level))
    }

    override fun getGameWithDifferences(level: Int): Either<Failure, GameWithDifferences> {
        return Either.Right(differenceCache.getGameWithDifferences(level))
    }

    override fun foundedCount(level: Int): Either<Failure, Int> {
        return Either.Right(gameCache.foundedCount(level))
    }

    override fun updateFoundedCount(level: Int): Either<Failure, None> {
        gameCache.updateFoundedCount(level)
        return Either.Right(None())
    }

    override fun differenceFounded(founded: Boolean, differenceId: Int): Either<Failure, None> {
        differenceCache.differenceFounded(founded, differenceId)
        return Either.Right(None())
    }

    override fun updateDifference(difference: DifferenceEntity): Either<Failure, None> {
        differenceCache.updateDifference(difference)
        return Either.Right(None())
    }

    override fun animateFoundedDifference(anim: Float, differenceId: Int): Either<Failure, None> {
        differenceCache.animateFoundedDifference(anim, differenceId)
        return Either.Right(None())
    }
}