package com.olbigames.finddifferencesgames.data.game

import com.olbigames.finddifferencesgames.domain.game.GameEntity

interface GameCache {

    fun insertGame(game: GameEntity)
    fun getAllGames(): List<GameEntity>

    fun getGame(level: Int): GameEntity
    fun foundedCount(level: Int): Int
    fun updateFoundedCount(level: Int)
    fun resetFoundedCount(level: Int)

    fun hiddenHintCount(level: Int): Int
    fun subtractOneHint(level: Int)
}