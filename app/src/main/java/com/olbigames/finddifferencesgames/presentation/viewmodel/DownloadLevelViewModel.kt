package com.olbigames.finddifferencesgames.presentation.viewmodel

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.olbigames.finddifferencesgames.MainActivity
import com.olbigames.finddifferencesgames.cache.SharedPrefsManager
import com.olbigames.finddifferencesgames.domain.HandleOnce
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.domain.game.LoadGamesSet
import com.olbigames.finddifferencesgames.utilities.Constants
import javax.inject.Inject

class DownloadLevelViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val loadGamesSetUseCase: LoadGamesSet
    ) : BaseViewModel() {

    val context = MainActivity.getContext()
    private val fileDirectory =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
    private var downloadLevelQuantity: Int? = 0

    private val _notifyLevelDownloaded = MutableLiveData<HandleOnce<Boolean>>()
    val notifyLevelDownloaded: LiveData<HandleOnce<Boolean>> = _notifyLevelDownloaded

    fun downloadGamesSet(levelQuantity: Int) {
        //downloadLevelQuantity = levelQuantity
        val startFrom = sharedPrefsManager.getStartLevel()
        loadGamesSetUseCase(LoadGamesSet.Params(startFrom, startFrom + levelQuantity, fileDirectory!!)) {
            it.either(
                ::handleFailure,
                ::handleGetGamesSet
            )
        }
    }

    private fun handleGetGamesSet(games: List<GameEntity>) {
        if (sharedPrefsManager.getGamesQuantity() != games.size) {
            downloadLevelQuantity = games.size - sharedPrefsManager.getGamesQuantity()
            sharedPrefsManager.saveGamesQuantity(games.size)
            val startFrom = sharedPrefsManager.getStartLevel()
            //val downloadedQuantity = games.size - sharedPrefsManager.getGamesQuantity()
            //sharedPrefsManager.setStartLevel(startFrom + downloadedQuantity)
            downloadLevelQuantity?.let { sharedPrefsManager.setStartLevel(startFrom + it) }
            _notifyLevelDownloaded.value = HandleOnce(true)
        } else {
            _notifyLevelDownloaded.value = HandleOnce(false)
        }
    }
}