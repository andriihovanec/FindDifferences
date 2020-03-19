package com.olbigames.finddifferencesgames.repository

import android.R
import android.util.Log
import android.widget.ArrayAdapter
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.olbigames.finddifferencesgames.db.diference.DifferenceDao
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import com.olbigames.finddifferencesgames.db.game.GameDao
import com.olbigames.finddifferencesgames.db.game.GameEntity
import kotlinx.coroutines.tasks.await
import java.io.File

class HomeRepository(
    private val gameDao: GameDao,
    private val differenceDao: DifferenceDao
) {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val imagesFolderRef = storageRef.child("images")

    private lateinit var database: DatabaseReference

    suspend fun allGames(): List<GameEntity> = gameDao.getAll()

    suspend fun insert(game: GameEntity) = gameDao.insert(game)

    suspend fun insertDifference(difference: DifferenceEntity) = differenceDao.insertDifference(difference)

    suspend fun downloadImageAsync(
        imageStorePath: String,
        file: File?
    ) {
        FirebaseAuth.getInstance().signInAnonymously().await()
        try {
            file?.let { imagesFolderRef.child(imageStorePath).getFile(it).await() }
            Log.d("FindDifferencesApp", file?.absolutePath.toString())
        } catch (e: FirebaseException) {
            Log.d("FindDifferencesApp", e.toString())
        }
    }
}