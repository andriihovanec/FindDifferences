package com.olbigames.finddifferencesgames.domain.game

import com.google.gson.Gson
import com.olbigames.finddifferencesgames.domain.difference.DifferencesListFromJson
import com.olbigames.finddifferencesgames.domain.hint.HiddenHintEntity
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.utilities.Constants.IMAGE_EXTENSION
import com.olbigames.finddifferencesgames.utilities.Constants.JSON_EXTENSION
import java.io.File
import java.io.IOException
import javax.inject.Inject

class LoadGamesSet @Inject constructor(
    private val gameRepository: GameRepository
) : UseCase<List<GameEntity>, LoadGamesSet.Params>() {

    companion object {
        const val FIRST_IMAGE_SUFFIX = 1
        const val SECOND_IMAGE_SUFFIX = 2
        const val FILE_NAME_PLACEHOLDER = "file name placeholder"
        const val LENGTH_IF_FILE_NO_EXIST = 0L
        const val BASE_PICTURE_NAME_TO_9 = "pic000"
        const val BASE_PICTURE_NAME_TO_99 = "pic00"
        const val BASE_PICTURE_NAME_BEFORE_99 = "pic0"
        const val RESOURCE_PACKAGE_NAME = "/downloaded_resource"
        const val BASE_DIFFERENCES_FILE_NAME = "game"
        const val BASE_HIDDEN_HINT_FILE_NAME = "hint"
    }

    private lateinit var mainImageRef: String
    private lateinit var differentImageRef: String
    private lateinit var differencesJsonRef: String
    private lateinit var hiddenHintJsonReference: String
    private lateinit var pathToGameResources: String
    private var mainFileName: String = FILE_NAME_PLACEHOLDER
    private var level: Int = 0

    override suspend fun run(params: Params): Either<Failure, List<GameEntity>> {
        pathToGameResources = params.pathToGameResources
        for (level in params.start..params.end) {
            this.level = level
            insertGameInDb()
            insertDifferenceInDb()
            insertHiddenHintInDb()
        }
        return gameRepository.allGames()
    }

    private suspend fun insertGameInDb() {
        val newMainFile = createMainImageFile()
        val newDifferentFile = createDifferentImageFile()
        gameRepository.downloadImageAsync(mainImageRef, newMainFile)
        gameRepository.downloadImageAsync(differentImageRef, newDifferentFile)
        newMainFile?.let {
            newDifferentFile?.let {
                if (it.length() != LENGTH_IF_FILE_NO_EXIST && newDifferentFile.length() != LENGTH_IF_FILE_NO_EXIST) {
                    gameRepository.insertGame(
                        GameEntity(
                            level,
                            "$mainFileName$IMAGE_EXTENSION",
                            newMainFile.absolutePath,
                            newDifferentFile.absolutePath
                        )
                    )
                }
            }
        }
    }

    private fun createMainImageFile(): File? {
        mainFileName = getFileName(FIRST_IMAGE_SUFFIX)
        mainImageRef = "$mainFileName$IMAGE_EXTENSION"
        return createFile(mainFileName, IMAGE_EXTENSION)
    }

    private fun createDifferentImageFile(): File? {
        val differentFileName = getFileName(SECOND_IMAGE_SUFFIX)
        differentImageRef = "$differentFileName$IMAGE_EXTENSION"
        return createFile(differentFileName, IMAGE_EXTENSION)
    }

    private fun getFileName(imageSuffix: Int): String {
        return when (level) {
            in 1..9 -> "$BASE_PICTURE_NAME_TO_9$level" + "_$imageSuffix"
            in 10..99 -> "$BASE_PICTURE_NAME_TO_99$level" + "_$imageSuffix"
            else -> "$BASE_PICTURE_NAME_BEFORE_99$level" + "_$imageSuffix"
        }
    }

    private fun createFile(fileName: String, extension: String): File? {
        val dir =
            File("$pathToGameResources$RESOURCE_PACKAGE_NAME")
        val file =
            File(dir, "$fileName$extension")
        try {
            if (!dir.exists()) {
                dir.mkdir()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private suspend fun insertDifferenceInDb() {
        val newDifferencesFile = createDifferenceFile()
        gameRepository.downloadDifferencesAsync(differencesJsonRef, newDifferencesFile)
        newDifferencesFile?.let {
            if (it.length() != LENGTH_IF_FILE_NO_EXIST) {
                val json = fileToJson(newDifferencesFile)
                val gameDifferences =
                    Gson().fromJson(json, DifferencesListFromJson::class.java)

                gameDifferences.differences.forEach { difference ->
                    gameRepository.insertDifference(difference)
                }
            }
        }
    }

    private fun createDifferenceFile(): File? {
        val differencesJsonName = "$BASE_DIFFERENCES_FILE_NAME$level"
        differencesJsonRef = "$differencesJsonName$JSON_EXTENSION"
        return createFile(differencesJsonName, JSON_EXTENSION)
    }

    private suspend fun insertHiddenHintInDb() {
        val newHiddenHintFile = createHiddenHintFile()
        gameRepository.downloadHiddenHintAsync(hiddenHintJsonReference, newHiddenHintFile)
        newHiddenHintFile?.let {
            if (it.length() != LENGTH_IF_FILE_NO_EXIST) {
                val json = fileToJson(newHiddenHintFile)
                val gameHiddenHint = Gson().fromJson(json, HiddenHintEntity::class.java)
                gameRepository.insertHiddenHint(gameHiddenHint)
            }
        }
    }

    private fun createHiddenHintFile(): File? {
        val hiddenHintJsonFileName = "$BASE_HIDDEN_HINT_FILE_NAME$level"
        hiddenHintJsonReference = "$hiddenHintJsonFileName$JSON_EXTENSION"
        return createFile(hiddenHintJsonFileName, JSON_EXTENSION)
    }

    private fun fileToJson(file: File?): String {
        return file!!.inputStream().bufferedReader().use { it.readText() }
    }

    data class Params(val start: Int, val end: Int, val pathToGameResources: String)
}