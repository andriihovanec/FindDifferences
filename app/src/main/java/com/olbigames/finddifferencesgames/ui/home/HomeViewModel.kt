package com.olbigames.finddifferencesgames.ui.home

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.olbigames.finddifferencesgames.Constants.IMAGE_EXTENSION
import com.olbigames.finddifferencesgames.Constants.JSON_EXTENSION
import com.olbigames.finddifferencesgames.db.AppDatabase
import com.olbigames.finddifferencesgames.db.diference.DifferencesListFromJson
import com.olbigames.finddifferencesgames.db.game.GameEntity
import com.olbigames.finddifferencesgames.extension.checkCurrentConnection
import com.olbigames.finddifferencesgames.repository.HomeRepository
import com.olbigames.finddifferencesgames.service.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class HomeViewModel(application: Application) : AndroidViewModel(application),
    HomeViewContract.ViewModel {

    private var repo: HomeRepository
    private var _gamesSet: MutableLiveData<Boolean> = MutableLiveData()
    private var _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    private var gameList: MutableList<GameEntity> = mutableListOf()
    private lateinit var mainImageRef: String
    private lateinit var differentImageRef: String
    private lateinit var differencesJsonRef: String
    private var levelSet: Int = 20
    private val fileDirectory =
        application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

    init {
        val gameDao = AppDatabase.getDatabase(application, viewModelScope).gameDao()
        val differenceDao = AppDatabase.getDatabase(application, viewModelScope).differenceDao()
        repo = HomeRepository(gameDao, differenceDao, Api.games)
        //getFirebaseToken()
        initGamesList(application)
    }

    private fun initGamesList(application: Application) {
        viewModelScope.launch(Dispatchers.Main) {
            val list = repo.allGames()
            gameList.clear()
            gameList.addAll(list)
            _gamesSet.value = list.isEmpty()

            if (list.isEmpty()) {
                if (checkCurrentConnection(application)) {
                    _isNetworkAvailable.value = true
                    getGamesSet(fileDirectory)
                } else {
                    _isNetworkAvailable.value = false
                }
            } else {
                _gamesSet.value = list.isEmpty()
            }
        }
    }

    fun getFirebaseToken() {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    val s = idToken
                    viewModelScope.launch(Dispatchers.IO) {
                        val l = repo.getAllGameDifferences(idToken!!, 1)
                        val r = l!!.differences
                    }
                } else {
                    val e = task.exception
                    val s = e?.message
                }
            }

    }

    private suspend fun getGamesSetAsync(pathToGameResources: String?) {
        if (gameList.count() != levelSet) {
            for (level in 1..levelSet) {
                insertGameInDb(pathToGameResources, level)
                insertDifferenceInDb(pathToGameResources, level)
            }
            addGameToList()
        }
    }

    private suspend fun insertGameInDb(pathToGameResources: String?, level: Int) {
        val mainFileName = getFileName(level, 1)
        mainImageRef = "$level/$mainFileName$IMAGE_EXTENSION"

        val differentFileName = getFileName(level, 2)
        differentImageRef = "$level/$differentFileName$IMAGE_EXTENSION"

        val newMainFile = createFile(pathToGameResources, mainFileName, IMAGE_EXTENSION)
        val newDifferentFile = createFile(pathToGameResources, differentFileName, IMAGE_EXTENSION)

        repo.downloadImageAsync(mainImageRef, newMainFile)
        repo.downloadImageAsync(differentImageRef, newDifferentFile)

        repo.insertGame(
            GameEntity(
                level,
                "$mainFileName$IMAGE_EXTENSION",
                newMainFile!!.absolutePath,
                newDifferentFile!!.absolutePath
            )
        )
    }

    private suspend fun insertDifferenceInDb(pathToGameResources: String?, level: Int) {
        val differencesJsonName = "game$level"
        differencesJsonRef = "$level/$differencesJsonName$JSON_EXTENSION"
        val newDifferencesJson = createFile(pathToGameResources, differencesJsonName, JSON_EXTENSION)
        repo.downloadDifferencesAsync(differencesJsonRef, newDifferencesJson)
        val gameDifferences = jsonToObject(fileToJson(newDifferencesJson)) as DifferencesListFromJson
        gameDifferences.differences.forEach { difference ->
            repo.insertDifference(difference)
        }
    }

    private suspend fun addGameToList() {
        gameList.clear()
        val list = repo.allGames()
        gameList.addAll(list)
        _gamesSet.value = list.isEmpty()
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

    override fun getGamesSet(pathToGameResources: String?) {
        viewModelScope.launch {
            getGamesSetAsync(pathToGameResources)
        }
    }

    override fun notifyAdapter(): LiveData<Boolean> = _gamesSet
    override fun notifyNetworkConnection(): LiveData<Boolean> = _isNetworkAvailable
}
