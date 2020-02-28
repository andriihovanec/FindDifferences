package com.olbigames.finddifferencesgames.repository

import com.olbigames.finddifferencesgames.db.GameDao
import com.olbigames.finddifferencesgames.db.GameEntity

class GameRepository(
    private val gameDao: GameDao
) {
    suspend fun findGame(level: String): GameEntity {
        return gameDao.findGame(level)
    }
}