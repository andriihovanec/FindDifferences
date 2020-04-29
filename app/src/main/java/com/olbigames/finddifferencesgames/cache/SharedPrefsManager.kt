package com.olbigames.finddifferencesgames.cache

import android.content.SharedPreferences
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class SharedPrefsManager @Inject constructor(private val prefs: SharedPreferences) {
    companion object {
        const val GAME_LEVEL = "game_level"
        const val GAMES_QUANTITY = "games_size"
        const val START_LEVEL = "start_level"
        const val HINT_COUNT = "hint_count"
    }

    fun setStartLevel(level: Int): Either<Failure, None> {
        prefs.edit().apply {
            putInt(START_LEVEL, level)
        }.apply()

        return Either.Right(None())
    }

    fun getStartLevel(): Int {
        return prefs.getInt(START_LEVEL, 1)
    }

    fun saveGameLevel(level: Int): Either<Failure, None> {
        prefs.edit().apply {
            putInt(GAME_LEVEL, level)
        }.apply()

        return Either.Right(None())
    }

    fun getGameLevel(): Int {
        return prefs.getInt(GAME_LEVEL, 1)
    }

    fun saveGamesQuantity(quantity: Int): Either<Failure, None> {
        prefs.edit().apply {
            putInt(GAMES_QUANTITY, quantity)
        }.apply()

        return Either.Right(None())
    }

    fun getGamesQuantity(): Int {
        return prefs.getInt(GAMES_QUANTITY, 0)
    }

    fun getHiddenHintCount(): Int {
        return prefs.getInt(HINT_COUNT, 10)
    }

    fun saveHiddenHintCount(count: Int): Either<Failure, None> {
        prefs.edit().apply {
            putInt(HINT_COUNT, count)
        }.apply()

        return Either.Right(None())
    }
}