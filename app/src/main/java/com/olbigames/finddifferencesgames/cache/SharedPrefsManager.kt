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
        const val NO_HINT = "no_hint"
        const val SOUND_EFFECT = "sound_effect"
        const val GESTURE_TIP_IS_SHOWN = "gesture tip is shown"
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
        return prefs.getInt(GAMES_QUANTITY, 20)
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

    fun ifNoMoreHint(): Boolean {
        return prefs.getBoolean(NO_HINT, false)
    }

    fun isNoMoreHint(noHint: Boolean) {
        prefs.edit().apply {
            putBoolean(NO_HINT, noHint)
        }.apply()
    }

    fun getSoundEffect(): Float {
        return prefs.getFloat(SOUND_EFFECT, 1f)
    }

    fun saveSoundEffect(count: Float): Either<Failure, None> {
        prefs.edit().apply {
            putFloat(SOUND_EFFECT, count)
        }.apply()

        return Either.Right(None())
    }

    fun isGestureTipShown(): Boolean {
        return prefs.getBoolean(GESTURE_TIP_IS_SHOWN, false)
    }

    fun gestureTipIsShown(isShown: Boolean) {
        prefs.edit().apply {
            putBoolean(GESTURE_TIP_IS_SHOWN, isShown)
        }.apply()
    }

}