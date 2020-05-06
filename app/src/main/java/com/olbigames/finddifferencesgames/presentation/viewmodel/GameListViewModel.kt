package com.olbigames.finddifferencesgames.presentation.viewmodel

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.olbigames.finddifferencesgames.MainActivity
import com.olbigames.finddifferencesgames.cache.SharedPrefsManager
import com.olbigames.finddifferencesgames.domain.HandleOnce
import com.olbigames.finddifferencesgames.domain.difference.DifferenceFounded
import com.olbigames.finddifferencesgames.domain.game.*
import com.olbigames.finddifferencesgames.domain.type.None
import com.olbigames.finddifferencesgames.extension.checkCurrentConnection
import com.olbigames.finddifferencesgames.utilities.Constants.GAMES_SET_20
import com.olbigames.finddifferencesgames.utilities.Constants.REFERENCE_POINT_20
import javax.inject.Inject

class GameListViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val allGameUseCase: GetAllGames,
    private val loadGamesSetUseCase: LoadGamesSet,
    private val resetFoundedCountUseCase: ResetFoundedCount,
    private val resetGameDifferencesUseCase: ResetGameDifferences,
    private val gameCompletedUseCase: GameCompleted,
    val getGameWithDifferenceUseCase: GetGameWithDifference
) : BaseViewModel() {

    val context = MainActivity.getContext()
    private val fileDirectory =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

    private var _gamesSet: MutableLiveData<Boolean> = MutableLiveData()
    private var _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    private var _gameReseated = MutableLiveData<HandleOnce<Boolean>>()

    private var gameList: MutableList<GameEntity> = mutableListOf()

    private fun handleGetGamesSet(games: List<GameEntity>) {
        resetList(games)
    }

    private fun handleAllGames(games: List<GameEntity>) {
        resetList(games)
        if (games.isEmpty()) {
            if (checkCurrentConnection(context)) {
                _isNetworkAvailable.value = true
                loadGamesSetAsync(fileDirectory)
            } else {
                _isNetworkAvailable.value = false
            }
        }
    }

    private fun resetList(games: List<GameEntity>) {
        gameList.clear()
        gameList.addAll(games)
        sharedPrefsManager.saveGamesQuantity(games.size)
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
        if (gameList.count() != GAMES_SET_20) {
            val startFrom = sharedPrefsManager.getStartLevel()
            sharedPrefsManager.setStartLevel(startFrom + GAMES_SET_20)
            loadGamesSetUseCase(LoadGamesSet.Params(startFrom, startFrom + REFERENCE_POINT_20, pathToGameResources!!)) {
                it.either(
                    ::handleFailure,
                    ::handleGetGamesSet
                )
            }
        }
    }

    fun resetFoundedCount(game: GameEntity) {
        gameCompletedUseCase(GameCompleted.Params(game.level, false))
        resetFoundedCountUseCase(ResetFoundedCount.Params(game.level))
        getGameWithDifferenceUseCase(GetGameWithDifference.Params(game.level)) {
            it.either(
                ::handleFailure,
                ::handleGameWithDifference
            )
        }
    }

    private fun handleGameWithDifference(gameWithDifferences: GameWithDifferences) {
        resetGameDifferencesUseCase(ResetGameDifferences.Params(gameWithDifferences.differences, false)) {
            it.either(
                ::handleFailure,
                ::handleDifferencesReset
            )
        }
    }

    private fun handleDifferencesReset(none: None) {
        _gameReseated.value = HandleOnce(true)
    }

    fun getList(): List<GameEntity> = gameList

    fun notifyAdapter(): LiveData<Boolean> = _gamesSet

    fun notifyNetworkConnection(): LiveData<Boolean> = _isNetworkAvailable

    fun notifyGameReseated() = _gameReseated
}
