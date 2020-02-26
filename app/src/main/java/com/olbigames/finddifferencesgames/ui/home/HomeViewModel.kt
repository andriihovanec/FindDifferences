package com.olbigames.finddifferencesgames.ui.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.olbigames.finddifferencesgames.db.GameDatabase
import com.olbigames.finddifferencesgames.db.GameEntity
import com.olbigames.finddifferencesgames.repository.GamesRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: GamesRepository
    val gamesSet: LiveData<List<GameEntity>>

    init {
        val gameDao = GameDatabase.getDatabase(application, viewModelScope).gameDao()
        repo = GamesRepository(gameDao)
        gamesSet = repo.allGames
    }

    private var folderName = "1"
    private var fileName = "pic0001"
    private var fileExtension = ".jpg"
    private lateinit var imageRef: String
    private var levelSet: Int = 20

    private val gameList: MutableList<GameEntity> = mutableListOf()

    fun getGamesSet(pathToGameResources: String?) {
        if (gameList.count() != levelSet) {
            for (level in 1..levelSet) {
                folderName = "$level"
                fileName = "pic000$level"
                imageRef = "$folderName/$fileName$fileExtension"

                viewModelScope.launch {
                    val newFile = createFile(pathToGameResources)
                    repo.downloadImageAsync(imageRef, newFile)
                    repo.insert(GameEntity(
                        folderName,
                        "$fileName$fileExtension",
                        Uri.fromFile(newFile).toString()
                    ))
                }

            }
        }
    }

    private fun createFile(path: String?): File? {
        val dir =
            File("$path/saved_images")
        val file =
            File(dir, "$fileName$fileExtension")

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
}
