package com.olbigames.finddifferencesgames.clean.domain.games

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.Either
import com.olbigames.finddifferencesgames.clean.domain.type.Failure
import com.olbigames.finddifferencesgames.clean.domain.type.None
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class DownloadImage @Inject constructor() : UseCase<None, DownloadImage.Params>() {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val imagesFolderRef = storageRef.child("images")

    override suspend fun run(params: Params): Either<Failure, None> {
        FirebaseAuth.getInstance().signInAnonymously().await()
        try {
            params.file?.let { imagesFolderRef.child(params.imageStorePath).getFile(it).await() }
            Log.d("FindDifferencesApp", params.file?.absolutePath.toString())
        } catch (e: FirebaseException) {
            Log.d("FindDifferencesApp", e.toString())
        }
        return Either.Right(None())
    }

    data class Params(val imageStorePath: String, val file: File?)
}