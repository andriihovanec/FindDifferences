package com.olbigames.finddifferencesgames.data.game

import com.olbigames.finddifferencesgames.data.difference.DifferenceCache
import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.domain.game.GameWithDifferences
import com.olbigames.finddifferencesgames.domain.game.GameRepository
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import java.io.File

class GameRepositoryImpl(
    private val gameCache: GameCache,
    private val differenceCache: DifferenceCache
) : GameRepository {

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

    override fun insertGame(game: GameEntity): Either<Failure, None> {
        gameCache.insertGame(game)
        return Either.Right(None())
    }

    override fun allGames(): Either<Failure, List<GameEntity>> {
        return Either.Right(gameCache.getAllGames())
    }

    override fun insertDifference(difference: DifferenceEntity): Either<Failure, None> {
        differenceCache.insertDifference(difference)
        return Either.Right(None())
    }

    override fun downloadImageAsync(imageStorePath: String, file: File?): Either<Failure, None> {
        return Either.Right(None())
    }

    override fun downloadDifferencesAsync(
        differenceStorePath: String,
        file: File?
    ): Either<Failure, None> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}