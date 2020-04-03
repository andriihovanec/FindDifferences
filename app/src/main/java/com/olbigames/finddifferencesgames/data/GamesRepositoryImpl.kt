package com.olbigames.finddifferencesgames.data

import com.olbigames.finddifferencesgames.cache.difference.DifferenceCache
import com.olbigames.finddifferencesgames.cache.game.GameCache
import com.olbigames.finddifferencesgames.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.games.GameEntity
import com.olbigames.finddifferencesgames.domain.games.GamesRepository
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import java.io.File

class GamesRepositoryImpl(
    private val gameCache: GameCache,
    private val differenceCache: DifferenceCache
) : GamesRepository {

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