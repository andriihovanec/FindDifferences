package com.olbigames.finddifferencesgames.ui.home

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.olbigames.finddifferencesgames.MainActivity
import com.olbigames.finddifferencesgames.domain.difference.AddDifference
import com.olbigames.finddifferencesgames.domain.difference.DifferencesListFromJson
import com.olbigames.finddifferencesgames.domain.difference.DownloadDifferences
import com.olbigames.finddifferencesgames.domain.game.*
import com.olbigames.finddifferencesgames.domain.type.None
import com.olbigames.finddifferencesgames.extension.checkCurrentConnection
import com.olbigames.finddifferencesgames.presentation.viewmodel.BaseViewModel
import com.olbigames.finddifferencesgames.utilities.Constants.IMAGE_EXTENSION
import com.olbigames.finddifferencesgames.utilities.Constants.JSON_EXTENSION
import java.io.File
import java.io.IOException
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val downloadImageUseCase: DownloadImage,
    private val downloadDifferencesUseCase: DownloadDifferences,
    private val allGameUseCase: GetAllGames,
    private val addGameUseCase: AddGame,
    private val addDifferencesUseCase: AddDifference
) : BaseViewModel(), HomeViewContract.ViewModel {

    private lateinit var mainImageRef: String
    private lateinit var differentImageRef: String
    private lateinit var differencesJsonRef: String
    private var levelSet: Int = 20
    val context = MainActivity.getContext()
    private val fileDirectory =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

    private var _gamesSet: MutableLiveData<Boolean> = MutableLiveData()
    private var _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    private var gameList: MutableList<GameEntity> = mutableListOf()

    private val _downloadedImage = MutableLiveData<None>()
    val downloadedImage = _downloadedImage

    private val _allGames = MutableLiveData<List<GameEntity>>()
    val allGames = _allGames

    private fun handleGameAdded(none: None) {}

    private fun handleDifferenceAdded(none: None) {}

    private fun handleDifferencesDownloaded(nine: None) {}

    private fun handleDownloadImage(none: None) {
        _downloadedImage.value = none
    }

    private fun handleAllGames(games: List<GameEntity>) {
        _allGames.value = games
        resetList(games)
        if (games.isEmpty()) {
            if (checkCurrentConnection(MainActivity.getContext())) {
                _isNetworkAvailable.value = true
                getGamesSet(fileDirectory)
            } else {
                _isNetworkAvailable.value = false
            }
        } else {
            _gamesSet.value = games.isEmpty()
        }
    }

    private fun resetList(games: List<GameEntity>) {
        gameList.clear()
        gameList.addAll(games)
        _gamesSet.value = games.isEmpty()
    }

    fun initGamesList() {
        allGameUseCase(None()) {
            it.either(
                ::handleFailure,
                ::handleAllGames
            )
        }
    }

    private fun getGamesSetAsync(pathToGameResources: String?) {
        if (gameList.count() != levelSet) {
            for (level in 1..levelSet) {
                insertGameInDb(pathToGameResources, level)
                insertDifferenceInDb(pathToGameResources, level)
            }
            addGameToList()
        }
    }

    private fun insertGameInDb(pathToGameResources: String?, level: Int) {
        val mainFileName = getFileName(level, 1)
        mainImageRef = "$level/$mainFileName$IMAGE_EXTENSION"

        val differentFileName = getFileName(level, 2)
        differentImageRef = "$level/$differentFileName$IMAGE_EXTENSION"

        val newMainFile = createFile(pathToGameResources, mainFileName, IMAGE_EXTENSION)
        val newDifferentFile = createFile(pathToGameResources, differentFileName, IMAGE_EXTENSION)

        downloadImageUseCase(DownloadImage.Params(mainImageRef, newMainFile)) {
            it.either(
                ::handleFailure,
                ::handleDownloadImage
            )
        }

        downloadImageUseCase(DownloadImage.Params(differentImageRef, newDifferentFile)) {
            it.either(
                ::handleFailure,
                ::handleDownloadImage
            )
        }

        addGameUseCase(
            AddGame.Params(
                GameEntity(
                    level,
                    "$mainFileName$IMAGE_EXTENSION",
                    newMainFile!!.absolutePath,
                    newDifferentFile!!.absolutePath
                )
            )
        ) {
            it.either(
                ::handleFailure,
                ::handleGameAdded
            )
        }
    }

    private fun insertDifferenceInDb(pathToGameResources: String?, level: Int) {
        val differencesJsonName = "game$level"
        differencesJsonRef = "$level/$differencesJsonName$JSON_EXTENSION"
        val newDifferencesJson =
            createFile(pathToGameResources, differencesJsonName, JSON_EXTENSION)

        downloadDifferencesUseCase(
            DownloadDifferences.Params(
                differencesJsonRef,
                newDifferencesJson
            )
        ) {
            it.either(
                ::handleFailure,
                ::handleDifferencesDownloaded
            )
        }

        val gameDifferences =
            jsonToObject(fileToJson(newDifferencesJson)) as DifferencesListFromJson

        gameDifferences.differences.forEach { difference ->
            addDifferencesUseCase(AddDifference.Params(difference)) {
                it.either(
                    ::handleFailure,
                    ::handleDifferenceAdded
                )
            }
        }
    }

    private fun addGameToList() {
        allGameUseCase(None()) {
            it.either(
                ::handleFailure,
                ::handleAllGames
            )
        }
    }

    private fun fileToJson(file: File?): String {
        return file!!.inputStream().bufferedReader().use { it.readText() }
    }

    private fun jsonToObject(json: String): Any {
        return Gson().fromJson(json, DifferencesListFromJson::class.java)
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

    override fun getList(): List<GameEntity> = gameList

    override fun getGamesSet(pathToGameResources: String?) = getGamesSetAsync(pathToGameResources)

    override fun notifyAdapter(): LiveData<Boolean> = _gamesSet

    override fun notifyNetworkConnection(): LiveData<Boolean> = _isNetworkAvailable
}
