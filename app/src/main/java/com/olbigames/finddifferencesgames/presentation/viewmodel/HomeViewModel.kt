package com.olbigames.finddifferencesgames.presentation.viewmodel

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.olbigames.finddifferencesgames.MainActivity
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.domain.game.GetAllGames
import com.olbigames.finddifferencesgames.domain.game.LoadGamesSet
import com.olbigames.finddifferencesgames.domain.type.None
import com.olbigames.finddifferencesgames.extension.checkCurrentConnection
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val allGameUseCase: GetAllGames,
    private val loadGamesSetUseCase: LoadGamesSet
) : BaseViewModel() {

    private var levelSet: Int = 20
    val context = MainActivity.getContext()
    private val fileDirectory =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

    private var _gamesSet: MutableLiveData<Boolean> = MutableLiveData()
    private var _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    private var gameList: MutableList<GameEntity> = mutableListOf()

    private val _allGames = MutableLiveData<List<GameEntity>>()
    val allGames = _allGames

    private fun handleGetGamesSet(games: List<GameEntity>) {
        allGameUseCase(None()) {
            it.either(
                ::handleFailure,
                ::handleAllGames
            )
        }
    }

    private fun handleLoadDifferences(none: None) {

    }

    private fun handleAllGames(games: List<GameEntity>) {
        _allGames.value = games
        resetList(games)
        if (games.isEmpty()) {
            if (checkCurrentConnection(context)) {
                _isNetworkAvailable.value = true
                loadGamesSetAsync(fileDirectory)
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

    private fun loadGamesSetAsync(pathToGameResources: String?) {
        if (gameList.count() != levelSet) {
            loadGamesSetUseCase(LoadGamesSet.Params(levelSet, pathToGameResources!!)) {
                it.either(
                    ::handleFailure,
                    ::handleGetGamesSet
                )
            }

        }
    }

    fun getList(): List<GameEntity> = gameList

    fun notifyAdapter(): LiveData<Boolean> = _gamesSet

    fun notifyNetworkConnection(): LiveData<Boolean> = _isNetworkAvailable
}
