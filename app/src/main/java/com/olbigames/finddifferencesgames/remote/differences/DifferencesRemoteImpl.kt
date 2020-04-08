package com.olbigames.finddifferencesgames.remote.differences

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.olbigames.finddifferencesgames.data.difference.DifferencesRemote
import java.io.File

class DifferencesRemoteImpl : DifferencesRemote {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val imagesFolderRef = storageRef.child("images")

    override fun downloadGameDifferences(differenceStorePath: String, file: File?) {
        FirebaseAuth.getInstance().signInAnonymously()
        try {
            file?.let { imagesFolderRef.child(differenceStorePath).getFile(it)
                .addOnSuccessListener { task ->
                    Log.d("FindDifferencesApp", task.toString())
                }
                .addOnFailureListener { e ->
                    Log.d("FindDifferencesApp", e.message.toString())
                }
            }
            Log.d("FindDifferencesApp", file?.absolutePath.toString())
        } catch (e: FirebaseException) {
            Log.d("FindDifferencesApp", e.toString())
        }
    }
}