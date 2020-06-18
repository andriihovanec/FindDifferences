package com.olbigames.finddifferencesgames.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.olbigames.finddifferencesgames.cache.SharedPrefsManager
import com.olbigames.finddifferencesgames.domain.game.*
import com.olbigames.finddifferencesgames.domain.type.None
import com.olbigames.finddifferencesgames.utilities.Constants.GAMES_SET_20
import com.olbigames.finddifferencesgames.utilities.Constants.REFERENCE_POINT_20
import com.olbigames.finddifferencesgames.utilities.HandleOnce
import javax.inject.Inject

class GameListViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val allGameUseCase: GetAllGames,
    private val resetFoundedCountUseCase: ResetFoundedCount,
    private val resetGameDifferencesUseCase: ResetGameDifferences,
    private val gameCompletedUseCase: GameCompleted,
    private val getLocalGameSetUseCase: GetLocalGameSet,
    val getGameWithDifferenceUseCase: GetGameWithDifference
) : BaseViewModel() {

    companion object {
        const val SOUND_ON = 1f
        const val SOUND_OF = 0f
    }

    private var _gamesSet: MutableLiveData<List<GameEntity>> = MutableLiveData()
    val gameSet = _gamesSet

    private var _gameReseated = MutableLiveData<HandleOnce<Boolean>>()
    val gameReseated = _gameReseated

    private var _isSoundOn = MutableLiveData<Boolean>()
    val soundOn = _isSoundOn

    init {
        initGamesList()
    }

    fun saveGameLevel(level: Int) {
        sharedPrefsManager.saveGameLevel(level)
    }

    fun switchSoundEffect() {
        val soundEffect = sharedPrefsManager.getSoundEffect()
        if (soundEffect == SOUND_ON) {
            sharedPrefsManager.saveSoundEffect(SOUND_OF)
            _isSoundOn.value = false
        } else if (soundEffect == SOUND_OF) {
            sharedPrefsManager.saveSoundEffect(SOUND_ON)
            _isSoundOn.value = true
        }
    }

    private fun initGamesList() {
        initState()
        allGameUseCase(None()) {
            it.either(
                ::handleFailure,
                ::handleAllGames
            )
        }
    }

    private fun initState() {
        val soundEffect = sharedPrefsManager.getSoundEffect()
        _isSoundOn.value = soundEffect == SOUND_ON
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

    private fun loadGamesFromResource() {
        val startFrom = sharedPrefsManager.getStartLevel()
        sharedPrefsManager.setStartLevel(startFrom + GAMES_SET_20)
        getLocalGameSetUseCase(
            GetLocalGameSet.Params(
                startFrom,
                startFrom + REFERENCE_POINT_20
            )
        ) {
            it.either(
                ::handleFailure,
                ::handleGetGamesSet
            )
        }
    }

    private fun handleAllGames(games: List<GameEntity>) {
        if (games.isEmpty()) loadGamesFromResource()
        else _gamesSet.value = games
    }

    private fun handleGameWithDifference(gameWithDifferences: GameWithDifferences) {
        resetGameDifferencesUseCase(
            ResetGameDifferences.Params(
                gameWithDifferences.differences,
                false
            )
        ) {
            it.either(
                ::handleFailure,
                ::handleDifferencesReset
            )
        }
    }

    private fun handleGetGamesSet(games: List<GameEntity>) {
        _gamesSet.value = games
    }

    private fun handleDifferencesReset(none: None) {
        _gameReseated.value =
            HandleOnce(true)
    }
}
