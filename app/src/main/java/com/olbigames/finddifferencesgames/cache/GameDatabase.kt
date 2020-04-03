package com.olbigames.finddifferencesgames.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.olbigames.finddifferencesgames.utilities.ListConverter
import com.olbigames.finddifferencesgames.cache.difference.DifferenceDao
import com.olbigames.finddifferencesgames.cache.game.GameDao
import com.olbigames.finddifferencesgames.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.games.GameEntity

@Database(
    entities = [GameEntity::class, DifferenceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
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