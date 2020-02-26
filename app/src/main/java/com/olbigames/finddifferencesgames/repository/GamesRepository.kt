package com.olbigames.finddifferencesgames.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.olbigames.finddifferencesgames.db.GameDao
import com.olbigames.finddifferencesgames.db.GameEntity
import kotlinx.coroutines.tasks.await
import java.io.File

class GamesRepository(
    private val gameDao: GameDao
) {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val imagesFolderRef = storageRef.child("images")

    val allGames: LiveData<List<GameEntity>> = gameDao.getAll()

    suspend fun insert(game: GameEntity) {
        gameDao.insert(game)
    }

    suspend fun insertSet(games: List<GameEntity>) {
        gameDao.insertList(games)
    }

    fun downloadImage(
        imageStorePath: String,
        file: File
    ) {
        imagesFolderRef.child(imageStorePath).getFile(file).addOnSuccessListener { fileDownloadTask ->
            var totalBytes = fileDownloadTask.totalByteCount
            var transferedByteCount = fileDownloadTask.bytesTransferred
            val imageUri = Uri.fromFile(file)
            Log.d("FindDifferencesApp", imageUri.toString())
        }.addOnFailureListener {
            Log.d("FindDifferencesApp", it.toString())
        }
    }

    suspend fun downloadImageAsync(
        imageStorePath: String,
        file: File?
    ) {
        FirebaseAuth.getInstance().signInAnonymously().await()
        try {
            file?.let { imagesFolderRef.child(imageStorePath).getFile(it).await() }
            Log.d("FindDifferencesApp", file.toString())
        } catch (e: FirebaseException) {
            Log.d("FindDifferencesApp", e.toString())
        }
    }
}