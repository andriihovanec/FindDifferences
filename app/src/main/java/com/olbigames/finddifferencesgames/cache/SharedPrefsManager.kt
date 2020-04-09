package com.olbigames.finddifferencesgames.cache

import android.content.SharedPreferences
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class SharedPrefsManager @Inject constructor(private val prefs: SharedPreferences) {
    companion object {
        const val GAME_LEVEL = "game_level"
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
}