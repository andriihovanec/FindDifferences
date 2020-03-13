package com.olbigames.finddifferencesgames.ui.game

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.olbigames.finddifferencesgames.db.AppDatabase
import com.olbigames.finddifferencesgames.db.game.GameEntity
import com.olbigames.finddifferencesgames.game.GameRenderer
import com.olbigames.finddifferencesgames.game.helper.RenderImageHelperImpl
import com.olbigames.finddifferencesgames.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GameViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var bitmapMain: Bitmap
    private lateinit var bitmapDifferent: Bitmap
    private var repo: GameRepository
    private lateinit var gameLevel: String
    private lateinit var gameRenderer: GameRenderer
    private var _foundedGame: MutableLiveData<GameEntity> = MutableLiveData()
    val foundedGame: LiveData<GameEntity> = _foundedGame
    private val _gameRender = MutableLiveData<GameRenderer>()
    val gameRender: LiveData<GameRenderer> = _gameRender

    init {
        val gameDao = AppDatabase.getDatabase(application, viewModelScope).gameDao()
        repo = GameRepository(gameDao)
    }

    fun startGame(level: String) {
        gameLevel = level
        if (gameLevel.toInt() > 20) {

        } else {
            viewModelScope.launch {
                _foundedGame.value = repo.findGame(level)
            }
        }
    }

    fun startNextGame() {
        var nextGameLevel = gameLevel.toInt()
        nextGameLevel++
        gameLevel = nextGameLevel.toString()
        startGame(nextGameLevel.toString())
    }

    fun startGameRenderer(displayW: Int, displayH: Int, bannerHeight: Int, gameLevel: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val game = repo.findGame(gameLevel.toString())
            viewModelScope.launch(Dispatchers.Main) {
                bitmapMain = BitmapFactory.decodeFile(game.pathToMainFile)
                bitmapDifferent = BitmapFactory.decodeFile(game.pathToDifferentFile)

                _gameRender.value = GameRenderer(
                    getApplication(),
                    viewModelScope,
                    displayW.toFloat(),
                    displayH.toFloat(),
                    bannerHeight.toFloat(),
                    repo,
                    gameLevel,
                    1f,
                    RenderImageHelperImpl(),
                    bitmapMain,
                    bitmapDifferent
                )
            }
        }
    }
}
