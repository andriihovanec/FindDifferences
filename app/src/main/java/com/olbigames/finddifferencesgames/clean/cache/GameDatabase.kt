package com.olbigames.finddifferencesgames.clean.cache

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.olbigames.finddifferencesgames.db.diference.DifferenceDao
import com.olbigames.finddifferencesgames.db.game.GameDao

abstract class GameDatabase : RoomDatabase() {

    abstract val gameDao: GameDao
    abstract val differenceDao: DifferenceDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {

            var instance = INSTANCE

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_level"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }

            return instance
        }
    }
}