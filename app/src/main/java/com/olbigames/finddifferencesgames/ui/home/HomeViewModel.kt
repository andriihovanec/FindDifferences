package com.olbigames.finddifferencesgames.ui.home

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.olbigames.finddifferencesgames.MainActivity
import com.olbigames.finddifferencesgames.clean.domain.games.DownloadImage
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.clean.presentation.viewmodel.BaseViewModel
import com.olbigames.finddifferencesgames.db.AppDatabase
import com.olbigames.finddifferencesgames.db.diference.DifferencesListFromJson
import com.olbigames.finddifferencesgames.db.game.GameEntity
import com.olbigames.finddifferencesgames.extension.checkCurrentConnection
import com.olbigames.finddifferencesgames.repository.HomeRepository
import com.olbigames.finddifferencesgames.utilities.Constants.IMAGE_EXTENSION
import com.olbigames.finddifferencesgames.utilities.Constants.JSON_EXTENSION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject


class HomeViewModel @Inject constructor(private val downloadImage: DownloadImage) : BaseViewModel(),
    HomeViewContract.ViewModel {

    private var repo: HomeRepository
    private var _gamesSet: MutableLiveData<Boolean> = MutableLiveData()
    private var _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    private var gameList: MutableList<GameEntity> = mutableListOf()
    private lateinit var mainImageRef: String
    private lateinit var differentImageRef: String
    private lateinit var differencesJsonRef: String
    private var levelSet: Int = 20
    val context = MainActivity.getContext()
    private val fileDirectory =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

    init {
        val gameDao = AppDatabase.getDatabase(context, viewModelScope).gameDao()
        val differenceDao = AppDatabase.getDatabase(context, viewModelScope).differenceDao()
        repo = HomeRepository(gameDao, differenceDao)
        //getFirebaseToken()
        initGamesList(context)
    }

    private val _downloadedImage = MutableLiveData<None>()
    val downloadedImage = _downloadedImage

    private fun handleDownloadImage(none: None) {
        _downloadedImage.value = none
    }

    private fun initGamesList(application: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            var list = listOf<GameEntity>()
            try {
                list = repo.allGames()
            } catch (e: Exception) {
                Log.d("DAO", "${e.message}")
            }

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

    /*fun getFirebaseToken() {
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

    }*/

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

        downloadImage(DownloadImage.Params(mainImageRef, newMainFile)) {
            it.either(
                ::handleFailure,
                ::handleDownloadImage
            )
        }

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
        val newDifferencesJson =
            createFile(pathToGameResources, differencesJsonName, JSON_EXTENSION)
        repo.downloadDifferencesAsync(differencesJsonRef, newDifferencesJson)
        val gameDifferences =
            jsonToObject(fileToJson(newDifferencesJson)) as DifferencesListFromJson
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
