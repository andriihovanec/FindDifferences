package com.olbigames.finddifferencesgames.domain.game

import android.util.Log
import com.google.gson.Gson
import com.olbigames.finddifferencesgames.domain.difference.DifferencesListFromJson
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.utilities.Constants
import java.io.File
import java.io.IOException
import javax.inject.Inject

class LoadGamesSet @Inject constructor(val gameRepository: GameRepository) :
    UseCase<List<GameEntity>, LoadGamesSet.Params>() {

    private lateinit var mainImageRef: String
    private lateinit var differentImageRef: String
    private lateinit var differencesJsonRef: String

    override suspend fun run(params: Params): Either<Failure, List<GameEntity>> {
        for (level in 1..params.set) {
            insertGameInDb(params.pathToGameResources, level)
        }
        Log.d("FindDifferencesApp", "Get games from db")
        return gameRepository.allGames()
    }

    private suspend fun insertGameInDb(pathToGameResources: String?, level: Int) {
        val mainFileName = getFileName(level, 1)
        mainImageRef = "$level/$mainFileName${Constants.IMAGE_EXTENSION}"

        val differentFileName = getFileName(level, 2)
        differentImageRef = "$level/$differentFileName${Constants.IMAGE_EXTENSION}"

        val newMainFile = createFile(pathToGameResources, mainFileName, Constants.IMAGE_EXTENSION)
        val newDifferentFile = createFile(
            pathToGameResources, differentFileName,
            Constants.IMAGE_EXTENSION
        )

        Log.d("FindDifferencesApp", "Started download main image $level")
        gameRepository.downloadImageAsync(mainImageRef, newMainFile)
        Log.d("FindDifferencesApp", "Started download another image $level")
        gameRepository.downloadImageAsync(differentImageRef, newDifferentFile)
        Log.d("FindDifferencesApp", "Insert game in db")
        gameRepository.insertGame(
            GameEntity(
                level,
                "$mainFileName${Constants.IMAGE_EXTENSION}",
                newMainFile!!.absolutePath,
                newDifferentFile!!.absolutePath
            )
        )

        insertDifferenceInDb(pathToGameResources, level)
    }

    private fun getFileName(level: Int, imageSuffix: Int): String {
        return when (level) {
            in 1..9 -> "pic000$level" + "_$imageSuffix"
            in 10..99 -> "pic00$level" + "_$imageSuffix"
            else -> "pic0$level" + "_$imageSuffix"
        }
    }

    private fun createFile(path: String?, fileName: String, extension: String): File? {
        val dir =
            File("$path/saved_images")
        val file =
            File(dir, "$fileName$extension")
        try {
            if (!dir.exists()) {
                dir.mkdir()
            }
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private suspend fun insertDifferenceInDb(pathToGameResources: String?, level: Int) {
        val differencesJsonName = "game$level"
        differencesJsonRef = "$level/$differencesJsonName${Constants.JSON_EXTENSION}"
        val newDifferencesJson =
            createFile(pathToGameResources, differencesJsonName, Constants.JSON_EXTENSION)

        gameRepository.downloadDifferencesAsync(differencesJsonRef, newDifferencesJson)

        val json = fileToJson(newDifferencesJson)
        val gameDifferences =
            jsonToObject(json) as DifferencesListFromJson

        gameDifferences.differences.forEach { difference ->
            gameRepository.insertDifference(difference)
        }
    }

    private fun fileToJson(file: File?): String {
        return file!!.inputStream().bufferedReader().use { it.readText() }
    }

    private fun jsonToObject(json: String): Any {
        return Gson().fromJson(json, DifferencesListFromJson::class.java)
    }

    data class Params(val set: Int, val pathToGameResources: String)
}