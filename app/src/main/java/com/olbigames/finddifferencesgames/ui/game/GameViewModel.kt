package com.olbigames.finddifferencesgames.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.olbigames.finddifferencesgames.db.GameDatabase
import com.olbigames.finddifferencesgames.db.GameEntity
import com.olbigames.finddifferencesgames.repository.GameRepository
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private var repo: GameRepository
    private lateinit var gameLevel: String
    private var _foundedGame: MutableLiveData<GameEntity> = MutableLiveData()
    val foundedGame: LiveData<GameEntity> = _foundedGame

    init {
        val gameDao = GameDatabase.getDatabase(application, viewModelScope).gameDao()
        repo = GameRepository(gameDao)
    }

    fun startGame(level: String) {
        gameLevel = level
        viewModelScope.launch {
            _foundedGame.value = repo.findGame(level)
        }
    }

    fun startNextGame() {
        var nextGameLevel = gameLevel.toInt()
        nextGameLevel++
        gameLevel = nextGameLevel.toString()
        startGame(nextGameLevel.toString())
    }
}
