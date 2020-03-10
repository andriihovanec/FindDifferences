package com.olbigames.finddifferencesgames.repository

import com.olbigames.finddifferencesgames.db.game.GameDao
import com.olbigames.finddifferencesgames.db.game.GameEntity

class GameRepository(
    private val gameDao: GameDao
) {
    suspend fun findGame(level: String): GameEntity {
        return gameDao.findGame(level)
    }

    fun addHint(i: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setHiddenHintFounded(level: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setDifferences(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun subtractOneHint() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}