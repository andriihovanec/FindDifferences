package com.olbigames.finddifferencesgames.domain.games

import com.olbigames.finddifferencesgames.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import java.io.File

interface GamesRepository {

    fun insertGame(game: GameEntity): Either<Failure, None>
    fun allGames(): Either<Failure, List<GameEntity>>

    fun insertDifference(difference: DifferenceEntity): Either<Failure, None>

    fun downloadImageAsync(imageStorePath: String, file: File?): Either<Failure, None>
    fun downloadDifferencesAsync(differenceStorePath: String, file: File?): Either<Failure, None>
}