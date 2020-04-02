package com.olbigames.finddifferencesgames.clean.data

import com.olbigames.finddifferencesgames.clean.cache.DifferenceCache
import com.olbigames.finddifferencesgames.clean.cache.GameCache
import com.olbigames.finddifferencesgames.clean.domain.games.GamesRepository
import com.olbigames.finddifferencesgames.clean.domain.type.Either
import com.olbigames.finddifferencesgames.clean.domain.type.Failure
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import com.olbigames.finddifferencesgames.db.game.GameEntity
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

    override fun downloadDifferencesAsync(differenceStorePath: String, file: File?): Either<Failure, None> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}