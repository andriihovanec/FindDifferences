package com.olbigames.finddifferencesgames.ui.home

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.olbigames.finddifferencesgames.Constants.FILE_EXTENSION
import com.olbigames.finddifferencesgames.db.GameDatabase
import com.olbigames.finddifferencesgames.db.GameEntity
import com.olbigames.finddifferencesgames.extension.checkCurrentConnection
import com.olbigames.finddifferencesgames.repository.HomeRepository
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
    private var levelSet: Int = 20
    private val fileDirectory =
        application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

    init {
        val gameDao = GameDatabase.getDatabase(application, viewModelScope).gameDao()
        repo = HomeRepository(gameDao)
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

    private suspend fun getGamesSetAsync(pathToGameResources: String?) {
        var mainFileName: String
        var differentFileName: String
        if (gameList.count() != levelSet) {
            for (level in 1..levelSet) {
                mainFileName = getFileName(level, 1)
                mainImageRef = "$level/$mainFileName$FILE_EXTENSION"

                differentFileName = getFileName(level, 2)
                differentImageRef = "$level/$differentFileName$FILE_EXTENSION"

                val newMainFile = createFile(pathToGameResources, mainFileName)
                val newDifferentFile = createFile(pathToGameResources, differentFileName)

                repo.downloadImageAsync(mainImageRef, newMainFile)
                repo.downloadImageAsync(differentImageRef, newDifferentFile)

                repo.insert(
                    GameEntity(
                        level.toString(),
                        "$mainFileName$FILE_EXTENSION",
                        Uri.fromFile(newMainFile).toString(),
                        Uri.fromFile(newDifferentFile).toString()
                    )
                )
            }
            gameList.clear()
            val list = repo.allGames()
            gameList.addAll(list)
            _gamesSet.value = list.isEmpty()
        }
    }

    private fun getFileName(level: Int, imageSuffix: Int): String {
        return when (level) {
            in 1..9 -> "pic000$level" + "_$imageSuffix"
            in 10..99 -> "pic00$level" + "_$imageSuffix"
            else -> "pic0$level" + "_$imageSuffix"
        }
    }

    private fun createFile(path: String?, fileName: String): File? {
        val dir =
            File("$path/saved_images")
        val file =
            File(dir, "$fileName$FILE_EXTENSION")
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
