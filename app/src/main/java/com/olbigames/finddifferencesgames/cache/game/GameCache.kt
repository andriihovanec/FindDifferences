package com.olbigames.finddifferencesgames.cache.game

import com.olbigames.finddifferencesgames.domain.games.GameEntity

interface GameCache {

    fun insertGame(game: GameEntity)
    fun getAllGames(): List<GameEntity>

    fun getGame(level: Int): GameEntity
    fun foundedCount(level: Int): Int
    fun updateFoundedCount(level: Int)
}