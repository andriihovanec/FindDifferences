package com.olbigames.finddifferencesgames.data.game

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.olbigames.finddifferencesgames.data.difference.DifferenceCache
import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.domain.game.GameRepository
import com.olbigames.finddifferencesgames.domain.game.GameWithDifferences
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import kotlinx.coroutines.tasks.await
import java.io.File

class GameRepositoryImpl(
    private val gameCache: GameCache,
    private val differenceCache: DifferenceCache
) : GameRepository {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val gameResFolderRef = storageRef.child("game_res")
    private val gameDifferencesFolderRef = gameResFolderRef.child("game_differences")

    override fun getGame(level: Int): Either<Failure, GameEntity> {
        return Either.Right(gameCache.getGame(level))
    }

    override fun getGameWithDifferences(level: Int): Either<Failure, GameWithDifferences> {
        return Either.Right(differenceCache.getGameWithDifferences(level))
    }

    override fun gameCompleted(level: Int, isCompleted: Boolean): Either<Failure, None> {
        gameCache.gameCompleted(level, isCompleted)
        return Either.Right(None())
    }

    override fun foundedCount(level: Int): Either<Failure, Int> {
        return Either.Right(gameCache.foundedCount(level))
    }

    override fun updateFoundedCount(level: Int): Either<Failure, None> {
        gameCache.updateFoundedCount(level)
        return Either.Right(None())
    }

    override fun resetFoundedCount(level: Int): Either<Failure, None> {
        gameCache.resetFoundedCount(level)
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

    override suspend fun downloadImageAsync(
        imageStorePath: String, file: File?
    ): Either<Failure, FileDownloadTask.TaskSnapshot> {

        Log.d("FindDifferencesApp", "Download image started")
        FirebaseAuth.getInstance().signInAnonymously().await()
        return try {
            Either.Right(file?.let { gameResFolderRef.child(imageStorePath).getFile(it)
                .addOnSuccessListener {
                    Log.d("FindDifferencesApp", "download completed ${file.name}")
                }
                .addOnFailureListener {
                    file.delete()
                    Log.d("FindDifferencesApp", "download failed ${file.name}")
                }
                .await()
            }!! )
        } catch (e: FirebaseException) {
            Log.d("FindDifferencesApp", "FirebaseException ${e.message}")
            Either.Left(Failure.ServerError)
        }
    }

    override suspend fun downloadDifferencesAsync(
        differenceStorePath: String,
        file: File?
    ): Either<Failure, FileDownloadTask.TaskSnapshot> {

        Log.d("FindDifferencesApp", "Download difference started")
        FirebaseAuth.getInstance().signInAnonymously().await()
        return try {
            Either.Right(file?.let { gameDifferencesFolderRef.child(differenceStorePath).getFile(it)
                .addOnSuccessListener { task ->
                    Log.d("FindDifferencesApp", "download completed ${file.name}")
                }
                .addOnFailureListener { e ->
                    file.delete()
                    Log.d("FindDifferencesApp", "download failed ${file.name}")
                }
                .await()
            }!! )
        } catch (e: FirebaseException) {
            Log.d("FindDifferencesApp", "FirebaseException ${e.message}")
            Either.Left(Failure.ServerError)
        }
    }
}