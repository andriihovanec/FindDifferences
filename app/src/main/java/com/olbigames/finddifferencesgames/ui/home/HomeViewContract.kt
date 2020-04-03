package com.olbigames.finddifferencesgames.ui.home

import androidx.lifecycle.LiveData
import com.olbigames.finddifferencesgames.clean.domain.games.GameEntity

interface HomeViewContract {

    interface ViewModel{
        fun notifyAdapter(): LiveData<Boolean>
        fun getList(): List<GameEntity>
        fun getGamesSet(pathToGameResources: String?)
        fun notifyNetworkConnection(): LiveData<Boolean>
    }
}