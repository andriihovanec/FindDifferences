package com.olbigames.finddifferencesgames.domain.game

import com.google.firebase.storage.FileDownloadTask
import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import java.io.File

interface GameRepository {

    fun getGame(level: Int): Either<Failure, GameEntity>
    fun getGameWithDifferences(level: Int): Either<Failure, GameWithDifferences>

    fun foundedCount(level: Int): Either<Failure, Int>
    fun updateFoundedCount(level: Int): Either<Failure, None>

    fun differenceFounded(founded: Boolean, differenceId: Int): Either<Failure, None>
    fun updateDifference(difference: DifferenceEntity): Either<Failure, None>
    fun animateFoundedDifference(anim: Float, differenceId: Int): Either<Failure, None>

    fun insertGame(game: GameEntity): Either<Failure, None>
    fun allGames(): Either<Failure, List<GameEntity>>

    fun insertDifference(difference: DifferenceEntity): Either<Failure, None>

    suspend fun downloadImageAsync(imageStorePath: String, file: File?): Either<Failure, FileDownloadTask.TaskSnapshot>
    suspend fun downloadDifferencesAsync(differenceStorePath: String, file: File?): Either<Failure, FileDownloadTask.TaskSnapshot>
}