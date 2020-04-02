package com.olbigames.finddifferencesgames.clean.cache

import com.olbigames.finddifferencesgames.db.game.GameEntity

interface GameCache {

    fun insertGame(game: GameEntity)
    fun getAllGames(): List<GameEntity>

    fun getGame(level: Int): GameEntity
    fun foundedCount(level: Int): Int
    fun updateFoundedCount(level: Int)
}